package com.g4mesoft.sound.format.mpeg;

import static com.g4mesoft.sound.format.mpeg.MPEGTables.BOUND_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L12_SCALEFACTOR_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L1_BITS_PER_SAMPLE_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L1_QUANTIZATION_C_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L1_QUANTIZATION_D_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.SIGN_MASK_TABLE;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;

public class MPEGAudioDataLayer1 implements IMPEGAudioData {

	private static final int SUBBANDS_PER_CH = 32;
	private static final int SAMPLES_PER_SB_CH = 12;
	
	/*
	 * Data read whilst decoding
	 */
	private final int[][] allocation;
	private final int[][] scalefactor;
	
	/*
	 * The decoded samples
	 */
	private final float[] samples;
	
	/*
	 * Fields used when decoding
	 */
	private int bound;
	
	public MPEGAudioDataLayer1() {
		allocation = new int[2][SUBBANDS_PER_CH];
		scalefactor = new int[2][SUBBANDS_PER_CH];
		samples = new float[getNumSamples()];
	}
	
	@Override
	public void readAudioData(AudioBitInputStream abis, MPEGFrame frame) throws IOException, CorruptedMPEGFrameException {
		MPEGHeader header = frame.getHeader();
		if (header.mode == MPEGHeader.JOINT_STEREO) {
			bound = BOUND_TABLE[header.mode_extension];
		} else {
			bound = SUBBANDS_PER_CH;
		}

		readAllocationBits(abis, frame);
		readScalefactors(abis, frame);
		readAndDecodeSamples(abis, frame);
		
		// We hit the end of the stream. This means
		// that some of the above read data is invalid.
		if (abis.isEndOfStream())
			throw new CorruptedMPEGFrameException("End of stream");
		
		// Synthesize samples
		frame.getSynthesisSubbandFilter().synthesizeSamples(samples, frame.nch, SAMPLES_PER_SB_CH);
		
		if (frame.nch == 1) {
			// If single channel, copy the samples to
			// the right channel.
			for (int i = 0; i < getNumSamples(); i += 2)
				samples[i + 1] = samples[i];
		}
	}

	private void readAllocationBits(AudioBitInputStream abis, MPEGFrame frame) throws IOException, CorruptedMPEGFrameException {
		int sb = 0;

		// Read allocation data
		for ( ; sb < bound; sb++) {
			for (int ch = 0; ch < frame.nch; ch++) {
				if ((allocation[ch][sb] = abis.readBits(4)) == 0xF)
					throw new CorruptedMPEGFrameException("Invalid allocation bits");
			}
		}
		
		// Joint stereo allocation data
		for ( ; sb < SUBBANDS_PER_CH; sb++) {
			// The allocation[1] is used when reading
			// the scalefactors.
			if ((allocation[0][sb] = allocation[1][sb] = abis.readBits(4)) == 0xF)
				throw new CorruptedMPEGFrameException("Invalid joint stereo allocation bits");
		}
	}
	
	private void readScalefactors(AudioBitInputStream abis, MPEGFrame frame) throws IOException, CorruptedMPEGFrameException {
		// Read scalefactors. Even in joint stereo
		// mode the scalefactors are transmitted
		// for both channels individually.
		for (int sb = 0; sb < SUBBANDS_PER_CH; sb++) {
			for (int ch = 0; ch < frame.nch; ch++) {
				if (allocation[ch][sb] != 0) {
					// Scalefactors are only transmitted if
					// the sample actually has allocated data
					if ((scalefactor[ch][sb] = abis.readBits(6)) == 0x3F)
						throw new CorruptedMPEGFrameException("Invalid scalefactor value");
				}
			}
		}
	}
	
	private void readAndDecodeSamples(AudioBitInputStream abis, MPEGFrame frame) throws IOException {
		// Read samples
		int sp = 0;
		for (int s = 0; s < SAMPLES_PER_SB_CH; s++) {
			int sb = 0;
			
			for ( ; sb < bound; sb++) {
				for (int ch = 0; ch < frame.nch; ch++) {
					int alloc = allocation[ch][sb];
					
					if (alloc != 0) {
						float sample = readAndRequantizeSample(abis, L1_BITS_PER_SAMPLE_TABLE[alloc]);
						
						// Apply scalefactors
						samples[sp + ch] = sample * L12_SCALEFACTOR_TABLE[scalefactor[ch][sb]];
					} else samples[sp + ch] = 0.0f;
				}

				sp += 2;
			}
			
			// In joint stereo mode we only read
			// one sample for both channels.
			for ( ; sb < SUBBANDS_PER_CH; sb++) {
				int alloc = allocation[0][sb];

				if (alloc != 0) {
					float sample = readAndRequantizeSample(abis, L1_BITS_PER_SAMPLE_TABLE[alloc]);

					// Apply scalefactors
					samples[sp++] = sample * L12_SCALEFACTOR_TABLE[scalefactor[0][sb]];
					samples[sp++] = sample * L12_SCALEFACTOR_TABLE[scalefactor[1][sb]];
				} else samples[sp++] = samples[sp++] = 0.0f;
			}
		}
	}
	
	private float readAndRequantizeSample(AudioBitInputStream abis, int nb) throws IOException {
		int sample = abis.readBits(nb);
		int mask = SIGN_MASK_TABLE[nb];
		
		// Turn the sample into a fraction
		float fraction = (sample & mask) != 0 ?
			(float)(sample ^ mask) / mask :
			(float)(sample) / mask - 1.0f;
		
		// Requantize values using formula:
		// s'' = (d + s''') * c
		fraction += L1_QUANTIZATION_D_TABLE[nb];
		return fraction * L1_QUANTIZATION_C_TABLE[nb];
	}

	@Override
	public void silence() {
		for (int i = 0; i < getNumSamples(); i++)
			samples[i] = 0.0f;
	}
	
	@Override
	public float[] getSamples() {
		return samples;
	}

	@Override
	public int getNumSamples() {
		return 2 * SAMPLES_PER_SB_CH * SUBBANDS_PER_CH;
	}
	
	@Override
	public int getSupportedLayer() {
		return MPEGHeader.LAYER_I;
	}
}
