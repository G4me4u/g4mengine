package com.g4mesoft.sound.format.mpeg;

import static com.g4mesoft.sound.format.mpeg.MPEGHeader.JOINT_STEREO;
import static com.g4mesoft.sound.format.mpeg.MPEGHeader.SINGLE_CHANNEL;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.BOUND_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.CHAN_RATE_INDEX_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L12_SCALEFACTOR_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L2_GROUPING_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L2_NBCODEWORD_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L2_QUANTIZATION_C_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L2_QUANTIZATION_D_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L2_QUANTIZATION_MSB_MASK;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L2_QUANTIZATION_STEPS_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L2_QUANTIZATION_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.L2_TABLEINDEX_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.NBAL_TABLE;
import static com.g4mesoft.sound.format.mpeg.MPEGTables.SBLIMIT_TABLE;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;

public class MPEGAudioDataLayer2 implements IMPEGAudioData {

	private static final int MAX_SUBBANDS_PER_CH = 32;
	private static final int NUM_GRANULES = 12;
	private static final int SAMPLES_PER_TRIPLET = 3;

	/*
	 * Data read whilst decoding
	 */
	private final int[][] allocation;
	private final int[][] scfsi;
	private final int[][][] scalefactor;
	private final float[][][] fractions;
	
	/*
	 * The final frame samples
	 */
	private final float[] samples;
	
	/*
	 * Fields used when decoding
	 */
	private int chan_rate;
	private int tableselect;
	private int sblimit;
	private int bound;
	
	public MPEGAudioDataLayer2() {
		allocation  = new int[2][MAX_SUBBANDS_PER_CH];
		scfsi       = new int[2][MAX_SUBBANDS_PER_CH];
		scalefactor = new int[2][MAX_SUBBANDS_PER_CH][SAMPLES_PER_TRIPLET];
		fractions   = new float[2][MAX_SUBBANDS_PER_CH][NUM_GRANULES * SAMPLES_PER_TRIPLET];
		samples     = new float[getNumSamples()];
	}
	
	@Override
	public void readAudioData(AudioBitInputStream abis, MPEGFrame frame) throws IOException, CorruptedMPEGFrameException {
		MPEGHeader header = frame.getHeader();
		
		if (header.mode == SINGLE_CHANNEL) {
			chan_rate = header.bitrate_index;
		} else {
			chan_rate = CHAN_RATE_INDEX_TABLE[header.bitrate_index - 4];
		}
		
		tableselect = L2_TABLEINDEX_TABLE[header.sampling_frequency][chan_rate];
		sblimit = SBLIMIT_TABLE[tableselect];
		if (header.mode == JOINT_STEREO) {
			bound = BOUND_TABLE[header.mode_extension];
		} else {
			bound = sblimit;
		}

		readAllocationBits(abis, frame);
		readScalefactorSelectionBits(abis, frame);
		readScalefactorBits(abis, frame);
		readAndDecodeSamples(abis, frame);
		
		if (abis.isEndOfStream())
			throw new CorruptedMPEGFrameException("End of stream");
			
		// Reorder samples
		reorder(frame.nch, sblimit);
		// Synthesis subband filter
		frame.getSynthesisSubbandFilter().synthesizeSamples(samples, frame.nch, NUM_GRANULES * SAMPLES_PER_TRIPLET);

		// If single channel, copy the
		// samples to the right channel.
		if (frame.nch == 1) {
			for (int i = 0; i < getNumSamples(); i += 2)
				samples[i + 1] = samples[i];
		}
	}

	private void readAllocationBits(AudioBitInputStream abis, MPEGFrame frame) throws IOException {
		// Read allocation
		int sb = 0;
		for ( ; sb < bound; sb++) {
			for (int ch = 0; ch < frame.nch; ch++)
				allocation[ch][sb] = abis.readBits(NBAL_TABLE[tableselect][sb]);
		}
		
		// Read allocation for both channels
		// in case of joint stereo
		for ( ; sb < sblimit; sb++)
			allocation[0][sb] = allocation[1][sb] = abis.readBits(NBAL_TABLE[tableselect][sb]);
	}
	
	private void readScalefactorSelectionBits(AudioBitInputStream abis, MPEGFrame frame) throws IOException {
		// Read scalefactor selection info
		for (int sb = 0; sb < sblimit; sb++) {
			for (int ch = 0; ch < frame.nch; ch++) {
				if (allocation[ch][sb] != 0)
					scfsi[ch][sb] = abis.readBits(2);
			}
		}
	}
	
	private void readScalefactorBits(AudioBitInputStream abis, MPEGFrame frame) throws IOException, CorruptedMPEGFrameException {
		// Read scalefactors
		for (int sb = 0; sb < sblimit; sb++) {
			for (int ch = 0; ch < frame.nch; ch++) {
				if (allocation[ch][sb] != 0) {
					int[] sfa = scalefactor[ch][sb];
					
					switch(scfsi[ch][sb]) {
					case 0x0:
						if ((sfa[0] = abis.readBits(6)) == 0x3F)
							throw new CorruptedMPEGFrameException("Invalid scalefactor bits");
						if ((sfa[1] = abis.readBits(6)) == 0x3F)
							throw new CorruptedMPEGFrameException("Invalid scalefactor bits");
						if ((sfa[2] = abis.readBits(6)) == 0x3F)
							throw new CorruptedMPEGFrameException("Invalid scalefactor bits");
						break;
					case 0x1:
						if ((sfa[0] = sfa[1] = abis.readBits(6)) == 0x3F)
							throw new CorruptedMPEGFrameException("Invalid scalefactor bits");
						if ((sfa[2] = abis.readBits(6)) == 0x3F)
							throw new CorruptedMPEGFrameException("Invalid scalefactor bits");
						break;
					case 0x2:
						if ((sfa[0] = sfa[1] = sfa[2] = abis.readBits(6)) == 0x3F)
							throw new CorruptedMPEGFrameException("Invalid scalefactor bits");
						break;
					default: // si == 0x3
						if ((sfa[0] = abis.readBits(6)) == 0x3F)
							throw new CorruptedMPEGFrameException("Invalid scalefactor bits");
						if ((sfa[1] = sfa[2] = abis.readBits(6)) == 0x3F)
							throw new CorruptedMPEGFrameException("Invalid scalefactor bits");
						break;
					}
				}
			}
		}
	}

	private void readAndDecodeSamples(AudioBitInputStream abis, MPEGFrame frame) throws IOException {
		// We're using the scalefactors and allocation
		// data in the following code. We have to make
		// sure it wasn't invalid.
		if (abis.isEndOfStream())
			throw new CorruptedMPEGFrameException("End of stream");
		
		// Read samples
		for (int gr = 0; gr < NUM_GRANULES; gr++) {
			int sb = 0;
			for ( ; sb < bound; sb++) {
				for (int ch = 0; ch < frame.nch; ch++) {
					int alloc = allocation[ch][sb];
					
					if (alloc != 0) {
						float sf = L12_SCALEFACTOR_TABLE[scalefactor[ch][sb][gr >> 2]];
						
						int step = L2_QUANTIZATION_TABLE[tableselect][sb][alloc];
						int nb = L2_NBCODEWORD_TABLE[step];
						
						if (L2_GROUPING_TABLE[step] != 0) {
							int nlevels = L2_QUANTIZATION_STEPS_TABLE[step];
							
							int c = abis.readBits(nb);
							for (int s = 0; s < SAMPLES_PER_TRIPLET; s++) {
								float sample = readAndRequantizeSample(c % nlevels, step);
								fractions[ch][sb][s + gr * 3] = sf * sample;
								c /= nlevels;
							}
						} else {
							for (int s = 0; s < SAMPLES_PER_TRIPLET; s++) {
								float sample = readAndRequantizeSample(abis.readBits(nb), step);
								fractions[ch][sb][s + gr * 3] = sf * sample;
							}
						}
					}
				}
			}
			
			for ( ; sb < sblimit; sb++) {
				int alloc = allocation[0][sb];
				
				if (alloc != 0) {
					float sf0 = L12_SCALEFACTOR_TABLE[scalefactor[0][sb][gr >> 2]];
					float sf1 = L12_SCALEFACTOR_TABLE[scalefactor[1][sb][gr >> 2]];
					
					int step = L2_QUANTIZATION_TABLE[tableselect][sb][alloc];
					int nb = L2_NBCODEWORD_TABLE[step];
					
					if (L2_GROUPING_TABLE[step] != 0) {
						int nlevels = L2_QUANTIZATION_STEPS_TABLE[step];

						int c = abis.readBits(nb);
						for (int s = 0; s < SAMPLES_PER_TRIPLET; s++) {
							float sample = readAndRequantizeSample(c % nlevels, step);
							fractions[0][sb][s + gr * 3] = sf0 * sample;
							fractions[1][sb][s + gr * 3] = sf1 * sample;
							c /= nlevels;
						}
					} else {
						for (int s = 0; s < SAMPLES_PER_TRIPLET; s++) {
							float sample = readAndRequantizeSample(abis.readBits(nb), step);
							fractions[0][sb][s + gr * 3] = sf0 * sample;
							fractions[1][sb][s + gr * 3] = sf1 * sample;
						}
					}
				}
			}
		}
	}
	
	private float readAndRequantizeSample(int sample, int step) {
		int mask = L2_QUANTIZATION_MSB_MASK[step];
		
		float fraction = (sample & mask) != 0 ?
			(float)(sample & (mask - 1)) / mask : 
			(float)(sample) / mask - 1.0f;
		
		fraction += L2_QUANTIZATION_D_TABLE[step];
		return fraction * L2_QUANTIZATION_C_TABLE[step];
	}
	
	private void reorder(int nch, int sblimit) {
		int sp = 0;
		int pa = nch == 1 ? 2 : 1;
		
		int s, sb, ch;
		
		for (int gr = 0; gr < NUM_GRANULES; gr++) {
			for (s = 0; s < SAMPLES_PER_TRIPLET; s++) {
				for (sb = 0 ; sb < sblimit; sb++) {
					for (ch = 0; ch < nch; ch++) {
						if (allocation[ch][sb] != 0) {
							samples[sp] = fractions[ch][sb][s + gr * 3];
						} else samples[sp] = 0.0f;
						sp += pa;
					}
				}

				for ( ; sb < MAX_SUBBANDS_PER_CH; sb++)
					samples[sp++] = samples[sp++] = 0.0f;
			}
		}
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
	public int getSupportedLayer() {
		return MPEGHeader.LAYER_II;
	}

	@Override
	public int getNumSamples() {
		return 2 * NUM_GRANULES * MAX_SUBBANDS_PER_CH * SAMPLES_PER_TRIPLET;
	}
}
