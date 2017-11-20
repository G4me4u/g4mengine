package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

import static com.g4mesoft.sound.format.mpeg.MPEGSideInformationLayer3.NUM_GRANULES;

import static com.g4mesoft.sound.format.mpeg.MPEGTables.L3_SLEN_TABLE;

public class MPEGMainDataLayer3 {

	private static final int NUM_SCALEFACTOR_L_BANDS = 8;
	private static final int NUM_SCALEFACTOR_S_BANDS = 12;
	private static final int NUM_WINDOWS = 3;
	
	private final float[][][] scalefac_l;
	private final float[][][][] scalefac_s;
	
	public MPEGMainDataLayer3() {
		scalefac_l = new float[NUM_GRANULES][2][NUM_SCALEFACTOR_L_BANDS];
		scalefac_s = new float[NUM_GRANULES][2][NUM_SCALEFACTOR_S_BANDS][NUM_WINDOWS];
	}
	
	public boolean readMainData(MP3BitStream bitStream, MPEGFrame frame, MPEGSideInformationLayer3 sideInfo) throws IOException {
		int ch, sfb, nb;
		
		for (int gr = 0; gr < NUM_GRANULES; gr++) {
			for (ch = 0; ch < frame.nch; ch++) {
				if (sideInfo.window_switching_flag[gr][ch] != 0 && sideInfo.block_type[gr][ch] == 2) {
					if (sideInfo.mixed_block_flag[gr][ch] != 0) {
						nb = L3_SLEN_TABLE[0][sideInfo.scalefac_compress[gr][ch]];
						for (sfb = 0; sfb < 8; sfb++) {
							scalefac_l[gr][ch][sfb] = bitStream.readBits(nb);
						}
						for (sfb = 3; sfb < 6; sfb++) {
							scalefac_s[gr][ch][sfb][0] = bitStream.readBits(nb);
							scalefac_s[gr][ch][sfb][1] = bitStream.readBits(nb);
							scalefac_s[gr][ch][sfb][2] = bitStream.readBits(nb);
						}
						nb = L3_SLEN_TABLE[1][sideInfo.scalefac_compress[gr][ch]];
						for ( ; sfb < 12; sfb++) {
							scalefac_s[gr][ch][sfb][0] = bitStream.readBits(nb);
							scalefac_s[gr][ch][sfb][1] = bitStream.readBits(nb);
							scalefac_s[gr][ch][sfb][2] = bitStream.readBits(nb);
						}
					} else {
						nb = L3_SLEN_TABLE[0][sideInfo.scalefac_compress[gr][ch]];
						for (sfb = 0; sfb < 6; sfb++) {
							scalefac_s[gr][ch][sfb][0] = bitStream.readBits(nb);
							scalefac_s[gr][ch][sfb][1] = bitStream.readBits(nb);
							scalefac_s[gr][ch][sfb][2] = bitStream.readBits(nb);
						}
						nb = L3_SLEN_TABLE[1][sideInfo.scalefac_compress[gr][ch]];
						for ( ; sfb < 12; sfb++) {
							scalefac_s[gr][ch][sfb][0] = bitStream.readBits(nb);
							scalefac_s[gr][ch][sfb][1] = bitStream.readBits(nb);
							scalefac_s[gr][ch][sfb][2] = bitStream.readBits(nb);
						}
					}
				} else {
					nb = L3_SLEN_TABLE[0][sideInfo.scalefac_compress[gr][ch]];
					if (sideInfo.scfsi[ch][0] == 0 || gr == 0) {
						for (sfb = 0; sfb < 6; sfb++)
							scalefac_l[gr][ch][sfb] = bitStream.readBits(nb);
					}
					if (sideInfo.scfsi[ch][1] == 0 || gr == 0) {
						for (sfb = 6; sfb < 11; sfb++)
							scalefac_l[gr][ch][sfb] = bitStream.readBits(nb);
					}
					nb = L3_SLEN_TABLE[1][sideInfo.scalefac_compress[gr][ch]];
					if (sideInfo.scfsi[ch][2] == 0 || gr == 0) {
						for (sfb = 11; sfb < 16; sfb++)
							scalefac_l[gr][ch][sfb] = bitStream.readBits(nb);
					}
					if (sideInfo.scfsi[ch][3] == 0 || gr == 0) {
						for (sfb = 16; sfb < 21; sfb++)
							scalefac_l[gr][ch][sfb] = bitStream.readBits(nb);
					}
				}
				
				readHuffmanCodeBits(gr, ch);
			}
		}
		
		return !bitStream.isEndOfStream();
	}

	private void readHuffmanCodeBits(int gr, int ch) {
		
	}
}
