package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;
import java.util.Arrays;

public class MPEGAudioDataLayer2 {

	private static final int MAX_SUBBANDS_PER_CH = 32;
	private static final int NUM_GRANULES = 12;
	private static final int SAMPLES_PER_TRIPLET = 3;
	
	private final int[][] allocation;
	private final int[][] scfsi;
	private final int[][][] scalefactor;
	private final float[][][] fractions;
	private final float[] samples;
	
	public MPEGAudioDataLayer2() {
		allocation  = new int[2][MAX_SUBBANDS_PER_CH];
		scfsi       = new int[2][MAX_SUBBANDS_PER_CH];
		scalefactor = new int[2][MAX_SUBBANDS_PER_CH][SAMPLES_PER_TRIPLET];
		fractions   = new float[2][MAX_SUBBANDS_PER_CH][NUM_GRANULES * SAMPLES_PER_TRIPLET];
		samples     = new float[2 * NUM_GRANULES * MAX_SUBBANDS_PER_CH * SAMPLES_PER_TRIPLET];
	}
	
	public boolean loadAudioData(MP3BitStream bitStream, MPEGFrame frame, MPEGSynthesisSubbandFilter synthesisFilter) throws IOException {
		int chan_rate = (frame.header.mode == MPEGHeader.SINGLE_CHANNEL) ? 
				frame.header.bitrate_index : MPEGTables.CHAN_RATE_INDEX_TABLE[frame.header.bitrate_index - 4];
		int table = MPEGTables.L2_TABLEINDEX_TABLE[frame.header.sampling_frequency][chan_rate];
		int sblimit = MPEGTables.SBLIMIT_TABLE[table];
		int bound = (frame.header.mode == MPEGHeader.JOINT_STEREO) ? 
				MPEGTables.BOUND_TABLE[frame.header.mode_extension] : sblimit;

		int sb, ch, s;
		int alloc, step;
		int c, nlevels;

		for (sb = 0; sb < bound; sb++)
			for (ch = 0; ch < frame.nch; ch++)
				allocation[ch][sb] = bitStream.readBits(MPEGTables.NBAL_TABLE[table][sb]);
		for ( ; sb < sblimit; sb++)
			allocation[0][sb] = allocation[1][sb] = bitStream.readBits(MPEGTables.NBAL_TABLE[table][sb]);
		
		for (sb = 0; sb < sblimit; sb++)
			for (ch = 0; ch < frame.nch; ch++)
				if (allocation[ch][sb] != 0)
					scfsi[ch][sb] = bitStream.readBits(2);
		
		for (sb = 0; sb < sblimit; sb++) {
			for (ch = 0; ch < frame.nch; ch++) {
				if (allocation[ch][sb] != 0) {
					int[] sfa = scalefactor[ch][sb];
					
					switch(scfsi[ch][sb]) {
					case 0x0:
						if ((sfa[0] = bitStream.readBits(6)) == 0x3F)
							return false;
						if ((sfa[1] = bitStream.readBits(6)) == 0x3F)
							return false;
						if ((sfa[2] = bitStream.readBits(6)) == 0x3F)
							return false;
						break;
					case 0x1:
						if ((sfa[0] = sfa[1] = bitStream.readBits(6)) == 0x3F)
							return false;
						if ((sfa[2] = bitStream.readBits(6)) == 0x3F)
							return false;
						break;
					case 0x2:
						if ((sfa[0] = sfa[1] = sfa[2] = bitStream.readBits(6)) == 0x3F)
							return false;
						break;
					default: // si == 0x3
						if ((sfa[0] = bitStream.readBits(6)) == 0x3F)
							return false;
						if ((sfa[1] = sfa[2] = bitStream.readBits(6)) == 0x3F)
							return false;
						break;
					}
				}
			}
		}
		
		for (int gr = 0; gr < NUM_GRANULES; gr++) {
			for (sb = 0; sb < bound; sb++) {
				for (ch = 0; ch < frame.nch; ch++) {
					alloc = allocation[ch][sb];
					if (alloc != 0) {
						step = MPEGTables.L2_QUANTIZATION_TABLE[table][sb][alloc];
						if (MPEGTables.L2_GROUPING_TABLE[step] != 0) {
							nlevels = MPEGTables.L2_QUANTIZATION_STEPS_TABLE[step];
							c = bitStream.readBits(MPEGTables.L2_NBCODEWORD_TABLE[step]);
							for (s = 0; s < SAMPLES_PER_TRIPLET; s++) {
								fractions[ch][sb][s + gr * 3] = requantize(c % nlevels, step);
								c /= nlevels;
							}
						} else {
							for (s = 0; s < SAMPLES_PER_TRIPLET; s++) {
								c = bitStream.readBits(MPEGTables.L2_NBCODEWORD_TABLE[step]);
								fractions[ch][sb][s + gr * 3] = requantize(c, step);
							}
						}
					} else {
						for (s = 0; s < SAMPLES_PER_TRIPLET; s++)
							fractions[ch][sb][s + gr * 3] = 0.0f;
					}
				}
			}
			
			for ( ; sb < sblimit; sb++) {
				alloc = allocation[0][sb];
				if (alloc != 0) {
					step = MPEGTables.L2_QUANTIZATION_TABLE[table][sb][alloc];
					if (MPEGTables.L2_GROUPING_TABLE[step] != 0) {
						c = bitStream.readBits(MPEGTables.L2_NBCODEWORD_TABLE[step]);
						nlevels = MPEGTables.L2_QUANTIZATION_STEPS_TABLE[step];
						for (s = 0; s < SAMPLES_PER_TRIPLET; s++) {
							fractions[0][sb][s + gr * 3] = fractions[1][sb][s + gr * 3] = requantize(c % nlevels, step);
							c /= nlevels;
						}
					} else {
						for (s = 0; s < SAMPLES_PER_TRIPLET; s++) {
							c = bitStream.readBits(MPEGTables.L2_NBCODEWORD_TABLE[step]);
							fractions[0][sb][s + gr * 3] = fractions[1][sb][s + gr * 3] = requantize(c, step);
						}
					}
				} else {
					for (s = 0; s < SAMPLES_PER_TRIPLET; s++)
						fractions[0][sb][s + gr * 3] = fractions[1][sb][s + gr * 3] = 0.0f;
				}
			}
		}

		if (bitStream.isEndOfStream())
			return false;

		// Rescale samples
		rescale(frame.nch, table, sblimit);
		// Synthesis subband filter
		synthesisFilter.synthesizeSamples(samples, frame.nch, NUM_GRANULES * SAMPLES_PER_TRIPLET);

		// If single channel, copy the
		// samples to the right channel.
		if (frame.nch == 1) {
			float sample;
			int i = 0;
			for (int gr = 0; gr < NUM_GRANULES; gr++) {
				for (s = 0; s < SAMPLES_PER_TRIPLET; s++) {
					for (sb = 0 ; sb < sblimit; sb++) {
						sample = samples[i++];
						samples[i++] = sample;
					}
				}
			}
		}
		
		return true;
	}

	private float requantize(int sample, int step) {
		int mask = MPEGTables.L2_QUANTIZATION_MSB_MASK[step];
		
		float fraction = (sample & mask) != 0 ?
			(float)(sample & (mask - 1)) / mask : 
			(float)(sample) / mask - 1.0f;
		
		fraction += MPEGTables.L2_QUANTIZATION_D_TABLE[step];
		fraction *= MPEGTables.L2_QUANTIZATION_C_TABLE[step];
		
		return fraction;
	}
	
	private void rescale(int nch, int table, int sblimit) {
		int sp = 0;
		int pa = nch == 1 ? 2 : 1;
		
		int s, sb, ch;
		
		float sf;
		
		for (int gr = 0; gr < NUM_GRANULES; gr++) {
			for (s = 0; s < SAMPLES_PER_TRIPLET; s++) {
				for (sb = 0 ; sb < sblimit; sb++) {
					for (ch = 0; ch < nch; ch++) {
						sf = MPEGTables.L12_SCALEFACTOR_TABLE[scalefactor[ch][sb][gr >> 2]];
						samples[sp] = (allocation[ch][sb] != 0) ? fractions[ch][sb][s + gr * 3] * sf : 0.0f;
						sp += pa;
					}
				}

				for ( ; sb < MAX_SUBBANDS_PER_CH; sb++)
					samples[sp++] = samples[sp++] = 0.0f;
			}
		}
	}

	public float[] getSamples() {
		return Arrays.copyOf(samples, 2 * NUM_GRANULES * MAX_SUBBANDS_PER_CH * SAMPLES_PER_TRIPLET);
	}
}
