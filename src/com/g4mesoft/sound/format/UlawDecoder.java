package com.g4mesoft.sound.format;

import com.g4mesoft.util.MemoryUtil;

public final class UlawDecoder {

	private static final int BIAS = 0x84;
	private static final int SIGN_BIT_MASK = 0x80;
	private static final int SEG_MASK = 0x70;
	private static final int SEG_SHIFT = 4;
	private static final int QUANT_MASK = 0x0F;
	
	private static final short[] ULAW_TABLE;
	
	static {
		ULAW_TABLE = new short[256];
		
		for (int i = 0; i < 256; i++)
			ULAW_TABLE[i] = preDecode(i);
	}
	
	private UlawDecoder() {
	}
	
	private static short preDecode(int ulaw) {
		ulaw = ~ulaw;

		int t = ((ulaw & QUANT_MASK) << 3) + BIAS;
		t <<= (ulaw & SEG_MASK) >>> SEG_SHIFT;
		
		return (short)((ulaw & SIGN_BIT_MASK) != 0 ? (BIAS - t) : (t - BIAS));
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
		return ULAW_TABLE[((int)ulaw) & 0xFF];
	}
	
	public static SoundEncoding getDecodedEncoding() {
		return SoundEncoding.PCM_SIGNED;
	}
}
