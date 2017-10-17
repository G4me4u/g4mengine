package com.g4mesoft.util;

public final class MemoryUtil {

	private MemoryUtil() {
	}
	
	public static long littleEndianToLong(byte[] buffer, int offset) {
		int h = ((buffer[offset + 0] & 0xFF) << 0) |
                ((buffer[offset + 1] & 0xFF) << 8) |
                ((buffer[offset + 2] & 0xFF) << 16) |
                ((buffer[offset + 3] & 0xFF) << 24);
        int l = ((buffer[offset + 4] & 0xFF) << 0) |
                ((buffer[offset + 5] & 0xFF) << 8) |
                ((buffer[offset + 6] & 0xFF) << 16) |
                ((buffer[offset + 7] & 0xFF) << 24);
        return (((long) h) << 32L) | ((long) l) & 0xFFFFFFFFL;
	}
	
	public static int littleEndianToInt(byte[] buffer, int offset) {
		return (buffer[offset + 0] & 0xFF) | 
			  ((buffer[offset + 1] & 0xFF) << 8) | 
			  ((buffer[offset + 2] & 0xFF) << 16) | 
			  ((buffer[offset + 3] & 0xFF) << 24);
	}

	public static long littleEndianToIntUnsignedLong(byte[] buffer, int offset) {
		return Integer.toUnsignedLong(littleEndianToInt(buffer, offset));
	}
	
	public static short littleEndianToShort(byte[] buffer, int offset) {
		return (short)((buffer[offset + 0] & 0xFF) | 
					  ((buffer[offset + 1] & 0xFF) << 8));
	}
	
	public static int littleEndianToShortUnsignedInt(byte[] buffer, int offset) {
		return Short.toUnsignedInt(littleEndianToShort(buffer, offset));
	}
	
	public static long bigEndianToLong(byte[] buffer, int offset) {
		int h = ((buffer[offset + 7] & 0xFF) << 0) |
                ((buffer[offset + 6] & 0xFF) << 8) |
                ((buffer[offset + 5] & 0xFF) << 16) |
                ((buffer[offset + 4] & 0xFF) << 24);
        int l = ((buffer[offset + 3] & 0xFF) << 0) |
                ((buffer[offset + 2] & 0xFF) << 8) |
                ((buffer[offset + 1] & 0xFF) << 16) |
                ((buffer[offset + 0] & 0xFF) << 24);
        return (((long) h) << 32L) | ((long) l) & 0xFFFFFFFFL;
	}
	
	public static int bigEndianToInt(byte[] buffer, int offset) {
		return (buffer[offset + 3] & 0xFF) | 
			  ((buffer[offset + 2] & 0xFF) << 8) | 
			  ((buffer[offset + 1] & 0xFF) << 16) | 
			  ((buffer[offset + 0] & 0xFF) << 24);
	}
	
	public static long bigEndianToIntUnsignedLong(byte[] buffer, int offset) {
		return Integer.toUnsignedLong(bigEndianToInt(buffer, offset));
	}

	public static short bigEndianToShort(byte[] buffer, int offset) {
		return (short)((buffer[offset + 1] & 0xFF) | 
					  ((buffer[offset + 0] & 0xFF) << 8));
	}
	
	public static int bigEndianToShortUnsignedInt(byte[] buffer, int offset) {
		return Short.toUnsignedInt(bigEndianToShort(buffer, offset));
	}
	
	public static void writeBigEndianLong(byte[] buffer, long value, int offset) {
		int i = (int) value;
		buffer[offset + 7] = (byte) ((i >>> 0) & 0xFF);
		buffer[offset + 6] = (byte) ((i >>> 8) & 0xFF);
		buffer[offset + 5] = (byte) ((i >>> 16) & 0xFF);
		buffer[offset + 4] = (byte) ((i >>> 24) & 0xFF);
		
		i = (int) (value >>> 32);
		buffer[offset + 3] = (byte) ((i >>> 0) & 0xFF);
		buffer[offset + 2] = (byte) ((i >>> 8) & 0xFF);
		buffer[offset + 1] = (byte) ((i >>> 16) & 0xFF);
		buffer[offset + 0] = (byte) ((i >>> 24) & 0xFF);
	}
	
	public static void writeBigEndianInt(byte[] buffer, int value, int offset) {
		buffer[offset + 3] = (byte)((value >>> 0) & 0xFF);
		buffer[offset + 2] = (byte)((value >>> 8) & 0xFF);
		buffer[offset + 1] = (byte)((value >>> 16) & 0xFF);
		buffer[offset + 0] = (byte)((value >>> 24) & 0xFF);
	}
	
	public static void writeBigEndianShort(byte[] buffer, short value, int offset) {
		buffer[offset + 1] = (byte)((value >>> 0) & 0xFF);
		buffer[offset + 0] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void writeLittleEndianLong(byte[] buffer, long value, int offset) {
		int i = (int) value;
		buffer[offset + 0] = (byte) ((i >>> 0) & 0xFF);
		buffer[offset + 1] = (byte) ((i >>> 8) & 0xFF);
		buffer[offset + 2] = (byte) ((i >>> 16) & 0xFF);
		buffer[offset + 3] = (byte) ((i >>> 24) & 0xFF);
		
		i = (int) (value >>> 32);
		buffer[offset + 4] = (byte) ((i >>> 0) & 0xFF);
		buffer[offset + 5] = (byte) ((i >>> 8) & 0xFF);
		buffer[offset + 6] = (byte) ((i >>> 16) & 0xFF);
		buffer[offset + 7] = (byte) ((i >>> 24) & 0xFF);
	}
	
	public static void writeLittleEndianInt(byte[] buffer, int value, int offset) {
		buffer[offset + 0] = (byte)((value >>> 0) & 0xFF);
		buffer[offset + 1] = (byte)((value >>> 8) & 0xFF);
		buffer[offset + 2] = (byte)((value >>> 16) & 0xFF);
		buffer[offset + 3] = (byte)((value >>> 24) & 0xFF);
	}
	
	public static void writeLittleEndianShort(byte[] buffer, short value, int offset) {
		buffer[offset + 0] = (byte)((value >>> 0) & 0xFF);
		buffer[offset + 1] = (byte)((value >>> 8) & 0xFF);
	}
}
