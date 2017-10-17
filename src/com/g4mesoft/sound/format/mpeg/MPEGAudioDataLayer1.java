package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;
import java.util.Arrays;

public class MPEGAudioDataLayer1 {

	private static final int SUBBANDS_PER_CH = 32;
	private static final int SAMPLES_PER_SB_CH = 12;
	
	private final int[][] allocation;
	private final int[][] scalefactor;
	private final float[] samples;
	
	public MPEGAudioDataLayer1() {
		allocation = new int[2][SUBBANDS_PER_CH];
		scalefactor = new int[2][SUBBANDS_PER_CH];
		samples = new float[2 * SAMPLES_PER_SB_CH * SUBBANDS_PER_CH];
	}
	
	public boolean loadAudioData(MP3BitStream bitStream, MPEGFrame frame, MPEGSynthesisSubbandFilter synthesisFilter) throws IOException {
		int bound = (frame.header.mode == MPEGHeader.JOINT_STEREO) ? MPEGTables.BOUND_TABLE[frame.header.mode_extension] : SUBBANDS_PER_CH;
		
		int sb, ch, nb;
		for (sb = 0; sb < bound; sb++)
			for (ch = 0; ch < frame.nch; ch++)
				if ((allocation[ch][sb] = bitStream.readBits(4)) == 0xF)
					return false;
		for ( ; sb < SUBBANDS_PER_CH; sb++)
			if ((allocation[0][sb] = allocation[1][sb] = bitStream.readBits(4)) == 0xF)
				return false;
		for (sb = 0; sb < SUBBANDS_PER_CH; sb++)
			for (ch = 0; ch < frame.nch; ch++)
				if (allocation[ch][sb] != 0)
					if ((scalefactor[ch][sb] = bitStream.readBits(6)) == 0x3F)
						return false;
		
		if (bitStream.isEndOfStream())
			return false;
		
		int sp = 0;
		int pa = frame.nch == 1 ? 2 : 1;

		for (int s = 0; s < SAMPLES_PER_SB_CH; s++) {
			for (sb = 0; sb < bound; sb++)
				for (ch = 0; ch < frame.nch; ch++) {
					nb = MPEGTables.L1_BITS_PER_SAMPLE_TABLE[allocation[ch][sb]];
					samples[sp] = (nb != 0) ? requantize(bitStream, nb) : 0.0f;
					
					sp += pa;
				}
			for ( ; sb < SUBBANDS_PER_CH; sb++) {
				nb = MPEGTables.L1_BITS_PER_SAMPLE_TABLE[allocation[0][sb]];
				samples[sp++] = samples[sp++] = (nb != 0) ? requantize(bitStream, nb) : 0.0f;
			}
		}
		
		if (bitStream.isEndOfStream())
			return false;
		
		// Rescale samples
		rescale(frame.nch);
		// Synthesis subband filter
		synthesisFilter.synthesizeSamples(samples, frame.nch, SAMPLES_PER_SB_CH);
		
		// If single channel, copy the
		// samples to the right channel.
		if (frame.nch == 1) {
			float sample;
			int i = 0;
			for (int s = 0; s < SAMPLES_PER_SB_CH; s++)
				for (sb = 0; sb < SUBBANDS_PER_CH; sb++) {
					sample = samples[i++];
					samples[i++] = sample;
				}
		}
		
		return true;
	}

	private float requantize(MP3BitStream bitStream, int nb) throws IOException {
		int sample = bitStream.readBits(nb);
		int mask = MPEGTables.SIGN_MASK_TABLE[nb];
		
		float fraction = (sample & mask) != 0 ?
			(float)(sample ^ mask) / mask :
			(float)(sample) / mask - 1.0f;

		fraction += MPEGTables.L1_QUANTIZATION_D_TABLE[nb];
		fraction *= MPEGTables.L1_QUANTIZATION_C_TABLE[nb];
		
		return fraction;
	}
	
	private void rescale(int nch) {
		int ch, s, sp;
		float sf;
		
		for (int sb = SUBBANDS_PER_CH - 1; sb >= 0; sb--) {
			for (ch = 0; ch < nch; ch++) {
				if (allocation[ch][sb] != 0) {
					sf = MPEGTables.L12_SCALEFACTOR_TABLE[scalefactor[ch][sb]];
					sp = ch + (sb << 1);
					for (s = SAMPLES_PER_SB_CH - 1; s >= 0; s--) {
						samples[sp] *= sf;
						sp += SUBBANDS_PER_CH * 2;
					}
				}
			}
		}
	}

	public float[] getSamples() {
		return Arrays.copyOf(samples, 2 * SUBBANDS_PER_CH * SAMPLES_PER_SB_CH);
	}
}
