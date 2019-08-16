package com.g4mesoft.sound.format;

import com.g4mesoft.util.MemoryUtil;

public final class AlawDecoder {

	private static final int SIGN_BIT_MASK = 0x80;
	private static final int SEG_MASK = 0x70;
	private static final int QUANT_MASK = 0x0F;
	
	private static final short[] ALAW_TABLE;
	
	static {
		ALAW_TABLE = new short[256];
		
		for (int i = 0; i < 256; i++)
			ALAW_TABLE[i] = preDecode(i);
	}
	
	private AlawDecoder() {
	}
	
	private static short preDecode(int alaw) {
		alaw ^= 0x55;

		int t = (alaw & QUANT_MASK) << 4;
		int e = (alaw & SEG_MASK) >>> 4;
	
		switch (e) {
		case 0:
			t += 0x08;
		case 1:
			t += 0x108;
		default:
			t += 0x108;
			t <<= e - 1;
		}
		
		return (short)((t & SIGN_BIT_MASK) != 0 ? t : -t);
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
			MemoryUtil.writeLittleEndianShort(dest, decode(src[srcPos++]), destPos);
			destPos += 2;
		}
	}
	
	public static short decode(byte ulaw) {
		return ALAW_TABLE[((int)ulaw) & 0xFF];
	}
	
	public static SoundEncoding getDecodedEncoding() {
		return SoundEncoding.PCM_SIGNED;
	}
}
