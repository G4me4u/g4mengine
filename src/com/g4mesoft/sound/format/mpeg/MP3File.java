package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;

import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.TagParsingException;
import com.g4mesoft.sound.format.info.TextAudioInfo;
import com.g4mesoft.sound.format.info.TextAudioInfo.TextAudioInfoType;
import com.g4mesoft.sound.format.info.id3.ID3v2Tag;

public class MP3File extends AudioFile {

	private static final String MPEG_FILE_TYPE = "MPG";
	
	private static final int MAX_FRAME_MARGIN = 8192;
	
	private static final int MPEG_V25 = 0x0;
	private static final int MPEG_V20 = 0x2;
	private static final int MPEG_V10 = 0x3;
	
	private static final int MPEG_INVALID = 0x1;
	
	private static final int LAYER_III = 0x1;
	private static final int LAYER_II = 0x2;
	private static final int LAYER_I = 0x3;

	private static final int LAYER_INVALID = 0x0;

	private static final int STEREO = 0x0;
	private static final int JOINT_STEREO = 0x1; // TODO: implement intensity stereo and MS stereo
	private static final int DUAL_CHANNEL = 0x2;
	private static final int SINGLE_CHANNEL = 0x3;

	// -1 is invalid values
	private static final int[] BITRATE_TABLE = new int[] {
		 // L3V2 L2V2 L1V2      L3V1 L2V1 L1V1
		 -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,
		 -1,   8,  32,  32,  -1,  32,  32,  32,
		 -1,  16,  48,  64,  -1,  40,  48,  64,
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
		 -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1
	};
	
	private static final int[] FREQUENCY_TABLE = new int[] {
		11025,    -1, 22050, 44100,
		12000,    -1, 24000, 48000,
		 8000,    -1, 16000, 32000,
		   -1,    -1,    -1,    -1
	};
	
	public static MP3File loadMP3(InputStream is) throws IOException, AudioParsingException {
		if (!is.markSupported())
			return null;
		is.mark(Integer.MAX_VALUE);
		
		ID3v2Tag tag = null;
		try {
			tag = ID3v2Tag.loadTag(is);
		} catch(TagParsingException e) {
			e.printStackTrace();
		}

		if (tag == null) {
			is.reset();
			return null;
		}
		
		System.out.println(tag.toString());
		
		TextAudioInfo type = (TextAudioInfo)tag.getFirstOccuringInformation(TextAudioInfoType.FILE_TYPE);
		if (type != null && !type.getValue()[0].startsWith(MPEG_FILE_TYPE)) {
			is.reset();
			return null;
		}
		
		MP3BitStream bs = new MP3BitStream(is);
		if (!findFrameSync(bs)) {
			is.reset();
			return null;
		}
		
		// 11 of 32 bits read by header sync
		int version = bs.readBits(2);			// 13
		int layer = bs.readBits(2);				// 15
		boolean crc = bs.readBits(1) == 0;		// 16
		int rateIndex = bs.readBits(4);			// 20
		int freqIndex = bs.readBits(2);			// 22
		boolean padding = bs.readBits(1) != 0;	// 23
		int privBit = bs.readBits(1);			// 24
		int channelMode = bs.readBits(2);		// 26
		int modeExt = bs.readBits(2);			// 28 TODO: implement mode EXT
		boolean cpyright = bs.readBits(1) != 0;	// 29
		boolean orig = bs.readBits(1) != 0;		// 30
		int emphasis = bs.readBits(2);			// 32

		if (bs.isEndOfStream() || version == MPEG_INVALID || layer == LAYER_INVALID) {
			is.reset();
			return null;
		}
		
		// rateIndex version layer
		//    1110      1     01   =   1110101 in table
		// NOTE: the bitrate table stores the result in kbits.
		int bitrate = BITRATE_TABLE[layer | ((version & 0x1) << 2) | (rateIndex << 3)] * 1000;

		// freqIndex version
		//    00       11   =   0011 in table
		int frequency  = FREQUENCY_TABLE[version | (freqIndex << 2)];
		
		// Bitrate or frequency is -1; invalid values.
		if (bitrate <= 0 || frequency <= 0) {
			is.reset();
			return null;
		}
		
		if (crc) { // Read CRC chunk
			bs.readBits(16);
		}

		System.out.println(bitrate + " bits per second");
		System.out.println(frequency + " Hz");
		
		int mainDataOffset = bs.readBits(9);
		if (channelMode == SINGLE_CHANNEL) { // mono
			int pb = bs.readBits(5);
			int scfsi = bs.readBits(4);
			
		} else {
			int pb = bs.readBits(3);
			int scfsi0 = bs.readBits(4);
			int scfsi1 = bs.readBits(4);
			
			
		}
		
		is.mark(-1);
		
		return null;
	}

	private static boolean findFrameSync(MP3BitStream bs) throws IOException {
		int p = 0;
		int b = bs.read();
		if (b == -1) 
			return false;
		while(true) {
			if (b == 0xFF) {
				if ((b = bs.readBits(3)) == -1) 
					return false;
				if (b == 0x7) 
					return true;
				b = bs.invalidateBufferedBits();
			} else {
				b = bs.read();
				if (b == -1) 
					return false;
			}

			if (p++ > MAX_FRAME_MARGIN)
				return false;
		}
	}
	
	@Override
	public AudioFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getData(byte[] dst, int srcPos, int dstPos, int len) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public long getLengthInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		InputStream is = MP3File.class.getResourceAsStream("/assets/ifiwereaboytest.mp3");
		long now = System.nanoTime();
		loadMP3(is);
		double t = (double)(System.nanoTime() - now) / 1000000.0;
		System.out.println("Loading time (ms)");
		System.out.println(t);
		is.close();
	}
}
