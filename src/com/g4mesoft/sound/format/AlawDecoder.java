package com.g4mesoft.sound.format;

import javax.sound.sampled.AudioFormat;

import com.g4mesoft.util.MemoryUtil;

public final class AlawDecoder {

	private static final short[] alawTable;
	
	static {
		alawTable = new short[256];
		
		for (int i = 0; i < 256; i++) {
			alawTable[i] = preDecode((byte)i);
		}
	}
	
	private AlawDecoder() {
	}
	
	private static short preDecode(byte alaw) {
		alaw ^= 0xD5;

		int sign = alaw & 0x80;
		int exponent = (alaw & 0x70) >> 4;
		int data = alaw & 0x0F;
	
		data <<= 4;
		data += 8;
		
		if (exponent != 0) {
			data += 0x100;

			if (exponent > 1)
				data <<= exponent - 1;
		}
		
		return (short)(sign == 0 ? data : -data);
	}
	
	public static byte[] decode(byte[] src) {
		return decode(src, src.length);
	}

	public static byte[] decode(byte[] src, int length) {
		byte[] dst = new byte[length << 1];
		decode(src, 0, dst, 0, length);
		return dst;
	}
	
	public static void decode(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
		int end = srcPos + length;
		while(srcPos < end) {
			short pcm = decode(src[srcPos++]);
			MemoryUtil.writeLittleEndianShort(dest, pcm, destPos);
			destPos += 2;
		}
	}
	
	public static short decode(byte ulaw) {
		return alawTable[((int)ulaw) & 0xFF];
	}
	
	public static AudioFormat.Encoding getDecodedEncoding() {
		return AudioFormat.Encoding.PCM_SIGNED;
	}
}
