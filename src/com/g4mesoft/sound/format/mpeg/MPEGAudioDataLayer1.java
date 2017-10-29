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
	
	public boolean readAudioData(MP3BitStream bitStream, MPEGFrame frame, MPEGSynthesisSubbandFilter synthesisFilter) throws IOException {
		int bound = (frame.header.mode == MPEGHeader.JOINT_STEREO) ? 
				MPEGTables.BOUND_TABLE[frame.header.mode_extension] : 
				SUBBANDS_PER_CH;
		
		int sb, ch;

		// Read allocation data
		for (sb = 0; sb < bound; sb++)
			for (ch = 0; ch < frame.nch; ch++)
				if ((allocation[ch][sb] = bitStream.readBits(4)) == 0xF)
					return false;
		for ( ; sb < SUBBANDS_PER_CH; sb++)
			if ((allocation[0][sb] = bitStream.readBits(4)) == 0xF)
				return false; // allocation[1][sb] is not used here.
		
		// Read scalefactors
		for (sb = 0; sb < SUBBANDS_PER_CH; sb++)
			for (ch = 0; ch < frame.nch; ch++)
				if (allocation[ch][sb] != 0)
					if ((scalefactor[ch][sb] = bitStream.readBits(6)) == 0x3F)
						return false;
		
		if (bitStream.isEndOfStream())
			return false;
		
		int sp = 0;
		int pa = frame.nch == 1 ? 2 : 1;

		int alloc;
		float sample;
		
		// Read samples
		for (int s = 0; s < SAMPLES_PER_SB_CH; s++) {
			for (sb = 0; sb < bound; sb++)
				for (ch = 0; ch < frame.nch; ch++) {
					if ((alloc = allocation[ch][sb]) != 0) {
						sample = requantize(bitStream, MPEGTables.L1_BITS_PER_SAMPLE_TABLE[alloc]);
						
						// Apply scalefactors
						samples[sp] = sample * MPEGTables.L12_SCALEFACTOR_TABLE[scalefactor[ch][sb]];
					} else samples[sp] = 0.0f;
					
					sp += pa;
				}
			for ( ; sb < SUBBANDS_PER_CH; sb++) {
				if ((alloc = allocation[0][sb]) != 0) {
					sample = requantize(bitStream, MPEGTables.L1_BITS_PER_SAMPLE_TABLE[alloc]);

					// Apply scalefactors
					samples[sp++] = sample * MPEGTables.L12_SCALEFACTOR_TABLE[scalefactor[0][sb]];
					samples[sp++] = sample * MPEGTables.L12_SCALEFACTOR_TABLE[scalefactor[1][sb]];
				} else samples[sp++] = samples[sp++] = 0.0f;
			}
		}
		
		if (bitStream.isEndOfStream())
			return false;
		
		// Synthesis subband filter
		synthesisFilter.synthesizeSamples(samples, frame.nch, SAMPLES_PER_SB_CH);
		
		// If single channel, copy the
		// samples to the right channel.
		if (frame.nch == 1) {
			sp = 0;
			for (int s = 0; s < SAMPLES_PER_SB_CH; s++) {
				for (sb = 0; sb < SUBBANDS_PER_CH; sb++) {
					sample = samples[sp++];
					samples[sp++] = sample;
				}
			}
		}
		
		return true;
	}

	private float requantize(MP3BitStream bitStream, int nb) throws IOException {
		int sample = bitStream.readBits(nb);
		int mask = MPEGTables.SIGN_MASK_TABLE[nb];
		
		// Turn the sample into a fraction
		float fraction = (sample & mask) != 0 ?
			(float)(sample ^ mask) / mask :
			(float)(sample) / mask - 1.0f;
		
		// Requantize values using formula:
		// s'' = (d + s''') * c
		fraction += MPEGTables.L1_QUANTIZATION_D_TABLE[nb];
		return fraction * MPEGTables.L1_QUANTIZATION_C_TABLE[nb];
	}
	
	public float[] getSamples() {
		return Arrays.copyOf(samples, 2 * SUBBANDS_PER_CH * SAMPLES_PER_SB_CH);
	}
}
