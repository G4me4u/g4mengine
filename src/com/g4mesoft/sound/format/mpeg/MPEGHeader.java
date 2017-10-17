package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

public class MPEGHeader {

	private static final int MAX_SYNC_SEARCHDEPTH = 8192;
	
	public static final int MPEG_V25 = 0x0;
	public static final int MPEG_V20 = 0x2;
	public static final int MPEG_V10 = 0x3;
	
	public static final int MPEG_INVALID = 0x1;
	
	public static final int LAYER_III = 0x1;
	public static final int LAYER_II = 0x2;
	public static final int LAYER_I = 0x3;

	public static final int LAYER_INVALID = 0x0;
	
	public static final int STEREO = 0x0;
	public static final int JOINT_STEREO = 0x1;
	public static final int DUAL_CHANNEL = 0x2;
	public static final int SINGLE_CHANNEL = 0x3;
	
	// The location in the stream
	// for the first byte in this
	// header (including frame-sync).
	public long byteLocation;
	
	// Version-related information
	public int version;
	public int layer;
	
	// Frame-related information
	public int protection_bit;
	public int bitrate_index;
	public int sampling_frequency;
	public int padding_bit;

	// Decoding-related information
	public int mode;
	public int mode_extension;
	
	// Audio-related information
	public int copyright;
	public int original;
	
	public int emphasis;
	
	public MPEGHeader() {
		version = MPEG_INVALID;
		layer = LAYER_INVALID;
	}

	private boolean findFrameSync(MP3BitStream bitStream) throws IOException {
		// NOTE: this is not necessary, as we're
		// calling bitStream.read(), which sets bitsLeft
		// to zero. So we're always on a byte-border.
		// bitStream.readBits(bitStream.getBitsLeft());
		
		int p = 1;
		int b = bitStream.read();
		while(!bitStream.isEndOfStream()) {
			if (b == 0xFF) {
				b = bitStream.readBits(3);
				if (b == 0x7) 
					return true;
				b = bitStream.invalidateBufferedBits();
			} else {
				b = bitStream.read();
			}

			if (p++ >= MAX_SYNC_SEARCHDEPTH)
				break;
		}
		
		return false;
	}
	
	public boolean readHeader(MP3BitStream bitStream) throws IOException {
		byteLocation = -1;
		
		if (!findFrameSync(bitStream))
			return false;
		
		// At this point, 11 of 32 bits 
		// should be read from header sync
		version = bitStream.readBits(2);
		layer = bitStream.readBits(2);
		
		protection_bit = bitStream.readBits(1);
		bitrate_index = bitStream.readBits(4);
		sampling_frequency = bitStream.readBits(2);
		padding_bit = bitStream.readBits(1);

		/* private_bit = */ bitStream.readBits(1);

		mode = bitStream.readBits(2);
		mode_extension = bitStream.readBits(2);
		copyright = bitStream.readBits(1);
		original = bitStream.readBits(1);
		
		emphasis = bitStream.readBits(2);
		
		// The header is 32 bits long, hence - 4
		byteLocation = bitStream.getBytesRead() - 4;
		
		return !bitStream.isEndOfStream() && isValidHeader();
	}
	
	public boolean isValidHeader() {
		return version != MPEG_INVALID && layer != LAYER_INVALID;
	}
}
