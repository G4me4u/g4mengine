package com.g4mesoft.sound.format;

import javax.sound.sampled.AudioFormat;

import com.g4mesoft.util.MemoryUtil;

public final class UlawDecoder {

	private static final int BIAS = 0x84;
	private static final short[] ulawTable;
	
	static {
		ulawTable = new short[256];
		
		for (int i = 0; i < 256; i++)
			ulawTable[i] = preDecode(i);
	}
	
	private UlawDecoder() {
	}
	
	private static short preDecode(int ulaw) {
		ulaw = ~ulaw;

		int sign = ulaw & 0x80;
		int exponent = (ulaw & 0x70) >>> 4;
		int data = ulaw & 0x0F;
	
		data |= 0x10;
		data <<= 1;
		data += 1;
		
		data <<= exponent + 2;
		data -= UlawDecoder.BIAS;
		
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
		return ulawTable[((int)ulaw) & 0xFF];
	}
	
	public static AudioFormat.Encoding getDecodedEncoding() {
		return AudioFormat.Encoding.PCM_SIGNED;
	}
}
