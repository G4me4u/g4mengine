package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;

public class MPEGHeader {

	/**
	 * The frame sync bit pattern is 11 ones. In hexadecimal this is given by 
	 * {@code 0x07FF}.
	 */
	private static final int FRAME_SYNC_PATTERN = 0x07FF;
	
	/**
	 * The size of the frame sync in bits.
	 */
	private static final int FRAME_SYNC_BIT_SIZE = 11;
	
	/*
	 * The size of all the parameters in the header. The sum of all the
	 * parameters will always equal 32 bits (including frame sync).
	 *   11 + 2 + 2 + 1 + 4 + 2 + 1 + 1 + 2 + 2 + 1 + 1 + 2 = 32
	 */
	private static final int VERSION_BIT_SIZE = 2;
	private static final int LAYER_BIT_SIZE = 2;
	private static final int PROTECTION_BIT_SIZE = 1;
	private static final int BITRATE_INDEX_BIT_SIZE = 4;
	private static final int SAMPLING_FREQ_BIT_SIZE = 2;
	private static final int PADDING_BIT_SIZE = 1;
	private static final int PRIVATE_BIT_SIZE = 1;
	private static final int MODE_BIT_SIZE = 2;
	private static final int MODE_EXT_BIT_SIZE = 2;
	private static final int COPYRIGHT_BIT_SIZE = 1;
	private static final int ORIGINAL_BIT_SIZE = 1;
	private static final int EMPHASIS_BIT_SIZE = 2;
	
	/**
	 * The size of a valid header in bytes.
	 */
	public static final int HEADER_BYTE_SIZE = 4;
	
	/*
	 * MPEG VERSION (MAJOR VERSION) CONSTANTS
	 */
	public static final int MPEG_V25 = 0x0;
	public static final int MPEG_V20 = 0x2;
	public static final int MPEG_V10 = 0x3;
	public static final int MPEG_INVALID = 0x1;

	/*
	 * LAYER IDENTIFICATION (MINOR VERSION) CONSTANTS
	 */
	public static final int LAYER_III = 0x1;
	public static final int LAYER_II = 0x2;
	public static final int LAYER_I = 0x3;
	public static final int LAYER_INVALID = 0x0;
	
	/*
	 * CHANNEL IDENTIFICATION CONSTANTS
	 */
	public static final int STEREO = 0x0;
	public static final int JOINT_STEREO = 0x1;
	public static final int DUAL_CHANNEL = 0x2;
	public static final int SINGLE_CHANNEL = 0x3;
	
	/**
	 * The location, in the bit stream, of the first byte in this header. This
	 * location includes the 11 bit frame sync.
	 */
	public long byteLocation;
	
	/**
	 * The major version of the MPEG frame. This indicates either MPEG2.5, MPEG2
	 * or MPEG1. (2 bits)
	 */
	public int version;
	
	/**
	 * The minor version of the MPEG frame. This indicates either Layer 1,
	 * Layer 2 or Layer 3. Each layer will be compressed using different methods
	 * and therefore have to be uncompressed using different parsers. (2 bits)
	 */
	public int layer;
	
	/**
	 * Whether this frame is 'protected' by CRC, meaning that any minor errors
	 * in the encoding can be fixed using the checksum. Zero indicates that
	 * there is a checksum. One indicates that no checksum is present in this
	 * frame. (1 bit)
	 */
	public int protection_bit;
	
	/**
	 * The lookup value for the bitrate. (4 bits)
	 */
	public int bitrate_index;
	
	/**
	 * The lookup value for the sampling frequency. (2 bits)
	 */
	public int sampling_frequency;
	
	/**
	 * Whether this MPEG frame contains padding at the end of the frame. One
	 * indicates that there is padding at the end of the frame. Zero indicates
	 * that there is no padding at the end of the frame. (1 bit)
	 */
	public int padding_bit;

	/**
	 * The lookup value for the MPEG mode. This indicates the number of channels
	 * in the MPEG frame. (2 bits)
	 */
	public int mode;
	
	/**
	 * The lookup value for the extended mode. (2 bits)
	 */
	public int mode_extension;
	
	/**
	 * Whether or not this MPEG frame is protected by copyright. One indicates
	 * that this frame is protected by copyright. Zero indicates that this frame
	 * is not protected by copyright. (1 bit)
	 */
	public int copyright;
	
	/**
	 * Whether or not this is the original MPEG frame, or if it was a copy. One
	 * indicates that this was the original. Zero indicates that is is a copy.
	 * (1 bit)
	 */
	public int original;
	
	/**
	 * The lookup value for the emphasis selection. (2 bits)
	 */
	public int emphasis;
	
	public MPEGHeader() {
		version = MPEG_INVALID;
		layer = LAYER_INVALID;
		
		invalidate();
	}

	public boolean readHeader(AudioBitInputStream abis) throws IOException {
		invalidate();
		
		if (!abis.findBitPattern(FRAME_SYNC_PATTERN, FRAME_SYNC_BIT_SIZE))
			return false;
		
		// At this point, 11 of 32 bits 
		// should be read from header sync
		version = abis.readBits(VERSION_BIT_SIZE);
		layer = abis.readBits(LAYER_BIT_SIZE);
		
		protection_bit = abis.readBits(PROTECTION_BIT_SIZE);
		bitrate_index = abis.readBits(BITRATE_INDEX_BIT_SIZE);
		sampling_frequency = abis.readBits(SAMPLING_FREQ_BIT_SIZE);
		padding_bit = abis.readBits(PADDING_BIT_SIZE);

		/* private_bit = */ abis.readBits(PRIVATE_BIT_SIZE);

		mode = abis.readBits(MODE_BIT_SIZE);
		mode_extension = abis.readBits(MODE_EXT_BIT_SIZE);
		copyright = abis.readBits(COPYRIGHT_BIT_SIZE);
		original = abis.readBits(ORIGINAL_BIT_SIZE);
		
		emphasis = abis.readBits(EMPHASIS_BIT_SIZE);
		
		if (!abis.isEndOfStream()) {
			// The header is 32 bits long, hence - 4
			byteLocation = abis.getBytesRead() - HEADER_BYTE_SIZE;

			return isValidHeader();
		}

		return false;
	}

	public void invalidate() {
		// Invalidate header by setting
		// the byte location to -1.
		byteLocation = -1;
	}
	
	public boolean isValidHeader() {
		return byteLocation != -1 && 
		       version != MPEG_INVALID && 
		       layer != LAYER_INVALID;
	}
}
