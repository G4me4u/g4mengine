package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;

public class MPEGSideInformationLayer3 {

	static final int NUM_SCFSI_BANDS = 4;
	static final int NUM_GRANULES = 2;
	
	public int main_data_begin;
	
	public final int[][] scfsi;
	public final int[][] part2_3_length;
	public final int[][] big_values;
	public final int[][] global_gain;
	public final int[][] scalefac_compress;
	public final int[][] window_switching_flag;
	public final int[][] block_type;
	public final int[][] mixed_block_flag;
	public final int[][] region0_count;
	public final int[][] region1_count;

	public final int[][] preflag;
	public final int[][] scalefac_scale;
	public final int[][] count1table_select;
	
	public final int[][][] table_select;
	public final int[][][] subblock_gain;
	
	public MPEGSideInformationLayer3() {
		main_data_begin = 0;
		
		scfsi                 = new int[2][NUM_SCFSI_BANDS];
		
		part2_3_length        = new int[NUM_GRANULES][2];
		big_values            = new int[NUM_GRANULES][2];
		global_gain           = new int[NUM_GRANULES][2];
		scalefac_compress     = new int[NUM_GRANULES][2];
		window_switching_flag = new int[NUM_GRANULES][2];
		block_type            = new int[NUM_GRANULES][2];
		mixed_block_flag      = new int[NUM_GRANULES][2];
		region0_count         = new int[NUM_GRANULES][2];
		region1_count         = new int[NUM_GRANULES][2];

		preflag               = new int[NUM_GRANULES][2];
		scalefac_scale        = new int[NUM_GRANULES][2];
		count1table_select    = new int[NUM_GRANULES][2];
		
		table_select          = new int[NUM_GRANULES][2][3];
		subblock_gain         = new int[NUM_GRANULES][2][3];
		
		
	}
	
	public void readSideInformation(AudioBitInputStream abis, MPEGFrame frame) throws IOException, CorruptedMPEGFrameException {
		main_data_begin = abis.readBits(9);
		/* private_bits = */ abis.readBits(frame.nch == 1 ? 5 : 3);
		
		for (int ch = 0; ch < frame.nch; ch++) {
			for (int scfsi_band = 0; scfsi_band < NUM_SCFSI_BANDS; scfsi_band++)
				scfsi[ch][scfsi_band] = abis.readBits(1);
		}
		
		for (int gr = 0; gr < NUM_GRANULES; gr++) {
			for (int ch = 0; ch < frame.nch; ch++) {
				part2_3_length[gr][ch] = abis.readBits(12);
				big_values[gr][ch] = abis.readBits(9);
				global_gain[gr][ch] = abis.readBits(8);
				scalefac_compress[gr][ch] = abis.readBits(4);
				if ((window_switching_flag[gr][ch] = abis.readBits(1)) != 0) {
					if ((block_type[gr][ch] = abis.readBits(2)) == 0)
						throw new CorruptedMPEGFrameException("Invalid block_type");
					mixed_block_flag[gr][ch] = abis.readBits(1);
					
					// region = [0, 1]
					table_select[gr][ch][0] = abis.readBits(5);
					table_select[gr][ch][1] = abis.readBits(5);

					// window = [0, 1, 2]
					subblock_gain[gr][ch][0] = abis.readBits(3);
					subblock_gain[gr][ch][1] = abis.readBits(3);
					subblock_gain[gr][ch][2] = abis.readBits(3);
					
					// Set default values
					if (mixed_block_flag[gr][ch] != 0) {
						region0_count[gr][ch] = 7;
					} else if (block_type[gr][ch] == 2) {
						region0_count[gr][ch] = 8;
					}

					region1_count[gr][ch] = 36;
				} else {
					// Set default values
					block_type[gr][ch] = 0;

					// region = [0, 1, 2]
					table_select[gr][ch][0] = abis.readBits(5);
					table_select[gr][ch][1] = abis.readBits(5);
					table_select[gr][ch][2] = abis.readBits(5);

					region0_count[gr][ch] = abis.readBits(4);
					region1_count[gr][ch] = abis.readBits(3);
				}
				
				preflag[gr][ch] = abis.readBits(1);
				scalefac_scale[gr][ch] = abis.readBits(1);
				count1table_select[gr][ch] = abis.readBits(1);
			}
		}
		
		if (abis.getBitsRemaining() != 0)
			throw new CorruptedMPEGFrameException("Side information is not byte aligned");
		if (abis.isEndOfStream())
			throw new CorruptedMPEGFrameException("End of stream");
	}
}
