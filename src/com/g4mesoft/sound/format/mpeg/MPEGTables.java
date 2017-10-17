package com.g4mesoft.sound.format.mpeg;

public class MPEGTables {

	// -1 is invalid values
	public static final int[] BITRATE_TABLE = new int[] {
		// L3V2 L2V2 L1V2      L3V1 L2V1 L1V1
		-1,  -1,  -1,  -1,  -1,  -1,  -1,  -1, // bitrate_index = 0000
		-1,   8,  32,  32,  -1,  32,  32,  32, // ...             0001
		-1,  16,  48,  64,  -1,  40,  48,  64, // etc.
		-1,  24,  56,  96,  -1,  48,  56,  96,
		-1,  32,  64, 128,  -1,  56,  64, 128,
		-1,  64,  80, 160,  -1,  64,  80, 160,
		-1,  80,  96, 192,  -1,  80,  96, 192,
		-1,  56, 112, 224,  -1,  96, 112, 224,
		-1,  64, 128, 256,  -1, 112, 128, 256,
		-1, 128, 160, 288,  -1, 128, 160, 288,
		-1, 160, 192, 320,  -1, 160, 192, 320,
		-1, 112, 224, 352,  -1, 192, 224, 352,
		-1, 128, 256, 384,  -1, 224, 256, 384,
		-1, 256, 320, 416,  -1, 256, 320, 416,
		-1, 320, 384, 448,  -1, 320, 384, 448,
		-1,  -1,  -1,  -1,  -1,  -1,  -1,  -1  // ...           = 1111
	};
	
	// Table used to decode the frequency of a frame
	public static final int[] FREQUENCY_TABLE = new int[] {
		// V25         V2      V1
		11025,    -1, 22050, 44100, // frequency_index = 00
		12000,    -1, 24000, 48000, 
		 8000,    -1, 16000, 32000, 
		   -1,    -1,    -1,    -1  // ...             = 11
	};
	
	// Table used to determine if a mode is allowed for
	// a specific bitrate in layer 2
	public static final int[][] L2_ALLOWED_MODES_TABLE = new int[][] {
		{  1,  1,  1,  1 }, // free format
		{  0,  0,  0,  1 },
		{  0,  0,  0,  1 },
		{  0,  0,  0,  1 },
		{  1,  1,  1,  1 },
		{  0,  0,  0,  1 },
		{  1,  1,  1,  1 },
		{  1,  1,  1,  1 },
		{  1,  1,  1,  1 },
		{  1,  1,  1,  1 },
		{  1,  1,  1,  1 },
		{  1,  1,  1,  0 },
		{  1,  1,  1,  0 },
		{  1,  1,  1,  0 },
		{  1,  1,  1,  0 },
		{  0,  0,  0,  0 }, // forbidden
	};
	
	// Bound table used in joint stereo to know what
	// subbands are in intensity stereo in layer 1 & 2
	public static final int[] BOUND_TABLE = new int[] {
		 4, // mode_extension = 00
		 8, 
		12, 
		16  // ...              11
	};
	
	// Table used to determine how many bits are allocated
	// for each sample in a subband in layer 1
	public static final int[] L1_BITS_PER_SAMPLE_TABLE = new int[] {
		 0, // allocation = 0
		 2,
		 3,
		 4,
		 5,
		 6,
		 7,
		 8,
		 9,
		10,
		11,
		12,
		13,
		14,
		15,
		-1  // ...        = 15 (invalid)
	};
	
	// Bit mask used to get the MSB of a
	// word of up to 16 bits length. Can
	// also be found by doing:
	//
	// SIGN_MASK_TABLE[0] = 0
	// SIGN_MASK_TABLE[i] = 1 << (i - 1)
	// where 0 > i >= 16
	public static final int[] SIGN_MASK_TABLE = new int[] {
		0x0000, // nb  = 0
		0x0001,
		0x0002,
		0x0004,
		0x0008,
		0x0010,
		0x0020,
		0x0040,
		0x0080,
		0x0100,
		0x0200,
		0x0400,
		0x0800,
		0x1000,
		0x2000,
		0x4000,
		0x8000  // ... = 16
	};
	
	// Table used to determine the bitrate per. channel in layer 2
	// Used when reading allocation bits from the bitstream.
	// NOTE: this table should be indexed as (bitrate - 4)
	public static final int[] CHAN_RATE_INDEX_TABLE = new int[] {
		1, 0, 2, 3, 4, 5, 6, 7, 8, 9, 10
	};

	// Table used to determine which of the following tables A-D
	// should be used for decoding of samples in layer 2
	public static final int[][] L2_TABLEINDEX_TABLE = new int[][] {
		{ 1, 2, 2, 0, 0, 0, 1, 1, 1, 1, 1 }, // 44100
		{ 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0 }, // 48000
		{ 1, 3, 3, 0, 0, 0, 1, 1, 1, 1, 1 }  // 32000
	};
	
	// Table used to determine sblimit for each of the
	// following tables A-D
	public static final int[] SBLIMIT_TABLE = new int[] {
		27, // table = 0
		30, 
		8, 
		12 // ...    = 3
	};
	
	// Table used to determine the number of bits used
	// to decode the allocation data in layer 2.
	public static final int[][] NBAL_TABLE = new int[][] {
		{ 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 0, 0, 0, 0, 0 },
		{ 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 0, 0 },
		{ 4, 4, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		{ 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
	};
	
	// Table used to determine the steps used for quantization
	public static final int[][][] L2_QUANTIZATION_TABLE = new int[][][] {
		{ { -1,  0,  2,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16 }, // table = 0
		  { -1,  0,  2,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16 },
		  { -1,  0,  2,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 } },
		
		{ { -1,  0,  2,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16 }, // table = 1
		  { -1,  0,  2,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16 },
		  { -1,  0,  2,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 16 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  2,  3,  4,  5, 16, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 } },

		{ { -1,  0,  1,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 }, // table = 2
		  { -1,  0,  1,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 } },
		
		{ { -1,  0,  1,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 }, // table = 3
		  { -1,  0,  1,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1,  0,  1,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 } }
	};
	
	// Table used to determine wether or not to use grouping
	// for a specific codeword in layer 2
	public static final int[] L2_GROUPING_TABLE = new int[] {
		1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};

	// Table used to determine the number of bits in each
	// sample_codeword
	public static final int[] L2_NBCODEWORD_TABLE = new int[] {
		5, 7, 3, 10, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16
	};

	// Table used to determine the number nlevels when decoding
	// the samplecode in layer 2
	public static final int[] L2_QUANTIZATION_STEPS_TABLE = new int[] {
		3, 5, 7, 9, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535
	};
	
	// Table used to find the MSB of the fractions in layer 2
	public static final int[] L2_QUANTIZATION_MSB_MASK = new int[] {
		0x0001, 
		0x0002, 
		0x0002, 
		0x0008, 
		0x0008, 
		0x0010, 
		0x0020, 
		0x0040, 
		0x0080, 
		0x0100,
		0x0200,
		0x0400,
		0x0800,
		0x1000,
		0x2000,
		0x4000,
		0x8000
	};
	
	// Table used to determine the coefficient used for
	// requantization in layer 1
	public static final float[] L1_QUANTIZATION_C_TABLE = new float[] {
		0.00000000000000f, // nb = 0
		2.00000000000000f, 
		1.33333337306976f, 
		1.14285719394684f, 
		1.06666672229767f, 
		1.03225803375244f, 
		1.01587307453156f, 
		1.00787401199341f, 
		1.00392162799835f, 
		1.00195693969727f, 
		1.00097751617432f, 
		1.00048851966858f, 
		1.00024425983429f, 
		1.00012207031250f, 
		1.00006103515625f, 
		1.00003051757812f  // nb = 15
	};

	// Table used to determine the coefficient used for
	// requantization in layer 2
	public static final float[] L2_QUANTIZATION_C_TABLE = new float[] {
		1.33333333333f,
		1.60000000000f,
		1.14285714286f,
		1.77777777777f,
		1.06666666666f,
		1.03225806452f,
		1.01587301587f,
		1.00787401575f,
		1.00392156863f,
		1.00195694716f,
		1.00097751711f,
		1.00048851979f,
		1.00024420024f,
		1.00012208522f,
		1.00006103888f,
		1.00003051851f,
		1.00001525902f
	};

	// Table used to determine the constant D used for
	// requantization in layer 1
	public static final float[] L1_QUANTIZATION_D_TABLE = new float[] {
		2.00000000000000f, // nb = 0
		1.00000000000000f, 
		0.50000000000000f, 
		0.25000000000000f, 
		0.12500000000000f, 
		0.06250000000000f, 
		0.03125000000000f, 
		0.01562500000000f, 
		0.00781250000000f, 
		0.00390625000000f, 
		0.00195312500000f, 
		0.00097656250000f, 
		0.00048828125000f, 
		0.00024414062500f, 
		0.00012207031250f, 
		0.00006103515625f  // nb = 15
	};

	// Table used to determine the constant D used for
	// requantization in layer 2
	public static final float[] L2_QUANTIZATION_D_TABLE = new float[] {
		0.50000000000f,
		0.50000000000f,
		0.25000000000f,
		0.50000000000f,
		0.12500000000f,
		0.06250000000f,
		0.03125000000f,
		0.01562500000f,
		0.00781250000f,
		0.00390625000f,
		0.00195312500f,
		0.00097656250f,
		0.00048828125f,
		0.00024414063f,
		0.00012207031f,
		0.00006103516f,
		0.00003051758f
	};
	
	// Scalefactors use the following formula:
	//
	// SCALEFACTOR_L12_TABLE[i] = 2.0 * pow(cbrt(0.5), i)
	// where 0 <= i <= 63
	public static final float[] L12_SCALEFACTOR_TABLE = new float[] {
		2.00000000000000f, 1.58740105196820f, 1.25992104989487f,
		1.00000000000000f, 0.79370052598410f, 0.62996052494744f,
		0.50000000000000f, 0.39685026299205f, 0.31498026247372f,
		0.25000000000000f, 0.19842513149602f, 0.15749013123686f,
		0.12500000000000f, 0.09921256574801f, 0.07874506561843f,
		0.06250000000000f, 0.04960628287401f, 0.03937253280921f,
		0.03125000000000f, 0.02480314143700f, 0.01968626640461f,
		0.01562500000000f, 0.01240157071850f, 0.00984313320230f,
		0.00781250000000f, 0.00620078535925f, 0.00492156660115f,
		0.00390625000000f, 0.00310039267963f, 0.00246078330058f,
		0.00195312500000f, 0.00155019633981f, 0.00123039165029f,
		0.00097656250000f, 0.00077509816991f, 0.00061519582514f,
		0.00048828125000f, 0.00038754908495f, 0.00030759791257f,
		0.00024414062500f, 0.00019377454248f, 0.00015379895629f,
		0.00012207031250f, 0.00009688727124f, 0.00007689947814f,
		0.00006103515625f, 0.00004844363562f, 0.00003844973907f,
		0.00003051757813f, 0.00002422181781f, 0.00001922486954f,
		0.00001525878906f, 0.00001211090890f, 0.00000961243477f,
		0.00000762939453f, 0.00000605545445f, 0.00000480621738f,
		0.00000381469727f, 0.00000302772723f, 0.00000240310869f,
		0.00000190734863f, 0.00000151386361f, 0.00000120155435f
	};
	
	public static final float[] L12_SYNTH_WINDOW_COEFFICIENTS_TABLE = new float[] {	
		 0.000000000f, -0.000015259f, -0.000015259f, -0.000015259f, 
		-0.000015259f, -0.000015259f, -0.000015259f, -0.000030518f, 
		-0.000030518f, -0.000030518f, -0.000030518f, -0.000045776f, 
		-0.000045776f, -0.000061035f, -0.000061035f, -0.000076294f, 
		-0.000076294f, -0.000091553f, -0.000106812f, -0.000106812f, 
		-0.000122070f, -0.000137329f, -0.000152588f, -0.000167847f, 
		-0.000198364f, -0.000213623f, -0.000244141f, -0.000259399f, 
		-0.000289917f, -0.000320435f, -0.000366211f, -0.000396729f, 
		-0.000442505f, -0.000473022f, -0.000534058f, -0.000579834f, 
		-0.000625610f, -0.000686646f, -0.000747681f, -0.000808716f, 
		-0.000885010f, -0.000961304f, -0.001037598f, -0.001113892f, 
		-0.001205444f, -0.001296997f, -0.001388550f, -0.001480103f, 
		-0.001586914f, -0.001693726f, -0.001785278f, -0.001907349f, 
		-0.002014160f, -0.002120972f, -0.002243042f, -0.002349854f, 
		-0.002456665f, -0.002578735f, -0.002685547f, -0.002792358f, 
		-0.002899170f, -0.002990723f, -0.003082275f, -0.003173828f, 
		 0.003250122f,  0.003326416f,  0.003387451f,  0.003433228f, 
		 0.003463745f,  0.003479004f,  0.003479004f,  0.003463745f, 
		 0.003417969f,  0.003372192f,  0.003280640f,  0.003173828f, 
		 0.003051758f,  0.002883911f,  0.002700806f,  0.002487183f, 
		 0.002227783f,  0.001937866f,  0.001617432f,  0.001266479f, 
		 0.000869751f,  0.000442505f, -0.000030518f, -0.000549316f, 
		-0.001098633f, -0.001693726f, -0.002334595f, -0.003005981f, 
		-0.003723145f, -0.004486084f, -0.005294800f, -0.006118774f, 
		-0.007003784f, -0.007919312f, -0.008865356f, -0.009841919f, 
		-0.010848999f, -0.011886597f, -0.012939453f, -0.014022827f, 
		-0.015121460f, -0.016235352f, -0.017349243f, -0.018463135f, 
		-0.019577026f, -0.020690918f, -0.021789551f, -0.022857666f, 
		-0.023910522f, -0.024932861f, -0.025909424f, -0.026840210f, 
		-0.027725220f, -0.028533936f, -0.029281616f, -0.029937744f, 
		-0.030532837f, -0.031005859f, -0.031387329f, -0.031661987f, 
		-0.031814575f, -0.031845093f, -0.031738281f, -0.031478882f, 
		 0.031082153f,  0.030517578f,  0.029785156f,  0.028884888f, 
		 0.027801514f,  0.026535034f,  0.025085449f,  0.023422241f, 
		 0.021575928f,  0.019531250f,  0.017257690f,  0.014801025f, 
		 0.012115479f,  0.009231567f,  0.006134033f,  0.002822876f, 
		-0.000686646f, -0.004394531f, -0.008316040f, -0.012420654f, 
		-0.016708374f, -0.021179199f, -0.025817871f, -0.030609131f, 
		-0.035552979f, -0.040634155f, -0.045837402f, -0.051132202f, 
		-0.056533813f, -0.061996460f, -0.067520142f, -0.073059082f, 
		-0.078628540f, -0.084182739f, -0.089706421f, -0.095169067f, 
		-0.100540161f, -0.105819702f, -0.110946655f, -0.115921021f, 
		-0.120697021f, -0.125259399f, -0.129562378f, -0.133590698f, 
		-0.137298584f, -0.140670776f, -0.143676758f, -0.146255493f, 
		-0.148422241f, -0.150115967f, -0.151306152f, -0.151962280f, 
		-0.152069092f, -0.151596069f, -0.150497437f, -0.148773193f, 
		-0.146362305f, -0.143264771f, -0.139450073f, -0.134887695f, 
		-0.129577637f, -0.123474121f, -0.116577148f, -0.108856201f, 
		 0.100311279f,  0.090927124f,  0.080688477f,  0.069595337f, 
		 0.057617188f,  0.044784546f,  0.031082153f,  0.016510010f, 
		 0.001068115f, -0.015228271f, -0.032379150f, -0.050354004f, 
		-0.069168091f, -0.088775635f, -0.109161377f, -0.130310059f, 
		-0.152206421f, -0.174789429f, -0.198059082f, -0.221984863f, 
		-0.246505737f, -0.271591187f, -0.297210693f, -0.323318481f, 
		-0.349868774f, -0.376800537f, -0.404083252f, -0.431655884f, 
		-0.459472656f, -0.487472534f, -0.515609741f, -0.543823242f, 
		-0.572036743f, -0.600219727f, -0.628295898f, -0.656219482f, 
		-0.683914185f, -0.711318970f, -0.738372803f, -0.765029907f, 
		-0.791213989f, -0.816864014f, -0.841949463f, -0.866363525f, 
		-0.890090942f, -0.913055420f, -0.935195923f, -0.956481934f, 
		-0.976852417f, -0.996246338f, -1.014617920f, -1.031936646f, 
		-1.048156738f, -1.063217163f, -1.077117920f, -1.089782715f, 
		-1.101211548f, -1.111373901f, -1.120223999f, -1.127746582f, 
		-1.133926392f, -1.138763428f, -1.142211914f, -1.144287109f, 
		 1.144989014f,  1.144287109f,  1.142211914f,  1.138763428f, 
		 1.133926392f,  1.127746582f,  1.120223999f,  1.111373901f, 
		 1.101211548f,  1.089782715f,  1.077117920f,  1.063217163f, 
		 1.048156738f,  1.031936646f,  1.014617920f,  0.996246338f, 
		 0.976852417f,  0.956481934f,  0.935195923f,  0.913055420f, 
		 0.890090942f,  0.866363525f,  0.841949463f,  0.816864014f, 
		 0.791213989f,  0.765029907f,  0.738372803f,  0.711318970f, 
		 0.683914185f,  0.656219482f,  0.628295898f,  0.600219727f, 
		 0.572036743f,  0.543823242f,  0.515609741f,  0.487472534f, 
		 0.459472656f,  0.431655884f,  0.404083252f,  0.376800537f, 
		 0.349868774f,  0.323318481f,  0.297210693f,  0.271591187f, 
		 0.246505737f,  0.221984863f,  0.198059082f,  0.174789429f, 
		 0.152206421f,  0.130310059f,  0.109161377f,  0.088775635f, 
		 0.069168091f,  0.050354004f,  0.032379150f,  0.015228271f, 
		-0.001068115f, -0.016510010f, -0.031082153f, -0.044784546f, 
		-0.057617188f, -0.069595337f, -0.080688477f, -0.090927124f, 
		 0.100311279f,  0.108856201f,  0.116577148f,  0.123474121f, 
		 0.129577637f,  0.134887695f,  0.139450073f,  0.143264771f, 
		 0.146362305f,  0.148773193f,  0.150497437f,  0.151596069f, 
		 0.152069092f,  0.151962280f,  0.151306152f,  0.150115967f, 
		 0.148422241f,  0.146255493f,  0.143676758f,  0.140670776f, 
		 0.137298584f,  0.133590698f,  0.129562378f,  0.125259399f, 
		 0.120697021f,  0.115921021f,  0.110946655f,  0.105819702f, 
		 0.100540161f,  0.095169067f,  0.089706421f,  0.084182739f, 
		 0.078628540f,  0.073059082f,  0.067520142f,  0.061996460f, 
		 0.056533813f,  0.051132202f,  0.045837402f,  0.040634155f, 
		 0.035552979f,  0.030609131f,  0.025817871f,  0.021179199f, 
		 0.016708374f,  0.012420654f,  0.008316040f,  0.004394531f, 
		 0.000686646f, -0.002822876f, -0.006134033f, -0.009231567f, 
		-0.012115479f, -0.014801025f, -0.017257690f, -0.019531250f, 
		-0.021575928f, -0.023422241f, -0.025085449f, -0.026535034f, 
		-0.027801514f, -0.028884888f, -0.029785156f, -0.030517578f, 
		 0.031082153f,  0.031478882f,  0.031738281f,  0.031845093f, 
		 0.031814575f,  0.031661987f,  0.031387329f,  0.031005859f, 
		 0.030532837f,  0.029937744f,  0.029281616f,  0.028533936f, 
		 0.027725220f,  0.026840210f,  0.025909424f,  0.024932861f, 
		 0.023910522f,  0.022857666f,  0.021789551f,  0.020690918f, 
		 0.019577026f,  0.018463135f,  0.017349243f,  0.016235352f, 
		 0.015121460f,  0.014022827f,  0.012939453f,  0.011886597f, 
		 0.010848999f,  0.009841919f,  0.008865356f,  0.007919312f, 
		 0.007003784f,  0.006118774f,  0.005294800f,  0.004486084f, 
		 0.003723145f,  0.003005981f,  0.002334595f,  0.001693726f, 
		 0.001098633f,  0.000549316f,  0.000030518f, -0.000442505f, 
		-0.000869751f, -0.001266479f, -0.001617432f, -0.001937866f, 
		-0.002227783f, -0.002487183f, -0.002700806f, -0.002883911f, 
		-0.003051758f, -0.003173828f, -0.003280640f, -0.003372192f, 
		-0.003417969f, -0.003463745f, -0.003479004f, -0.003479004f, 
		-0.003463745f, -0.003433228f, -0.003387451f, -0.003326416f, 
		 0.003250122f,  0.003173828f,  0.003082275f,  0.002990723f, 
		 0.002899170f,  0.002792358f,  0.002685547f,  0.002578735f, 
		 0.002456665f,  0.002349854f,  0.002243042f,  0.002120972f, 
		 0.002014160f,  0.001907349f,  0.001785278f,  0.001693726f, 
		 0.001586914f,  0.001480103f,  0.001388550f,  0.001296997f, 
		 0.001205444f,  0.001113892f,  0.001037598f,  0.000961304f, 
		 0.000885010f,  0.000808716f,  0.000747681f,  0.000686646f, 
		 0.000625610f,  0.000579834f,  0.000534058f,  0.000473022f, 
		 0.000442505f,  0.000396729f,  0.000366211f,  0.000320435f, 
		 0.000289917f,  0.000259399f,  0.000244141f,  0.000213623f, 
		 0.000198364f,  0.000167847f,  0.000152588f,  0.000137329f, 
		 0.000122070f,  0.000106812f,  0.000106812f,  0.000091553f, 
		 0.000076294f,  0.000076294f,  0.000061035f,  0.000061035f, 
		 0.000045776f,  0.000045776f,  0.000030518f,  0.000030518f, 
		 0.000030518f,  0.000030518f,  0.000015259f,  0.000015259f, 
		 0.000015259f,  0.000015259f,  0.000015259f,  0.000015259f
	};
	
	public static final float[] L12_SYNTH_SUBBAND_FILER_TABLE = new float[64 * 32];
	
	static {
		int tp = 0;
		for (int i = 0; i < 64; i++)
			for (int k = 0; k < 32; k++)
				L12_SYNTH_SUBBAND_FILER_TABLE[tp++] = (float)Math.cos((16.0 + i) * (2.0 * k + 1.0) * Math.PI / 64.0);
	}
}
