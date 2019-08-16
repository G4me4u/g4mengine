package com.g4mesoft.util;

import com.g4mesoft.math.MathUtils;

public final class MemoryUtil {

	private static final int EXTENDED_EXPONENT_BIAS = 0x3FFE;
	
	private MemoryUtil() {
	}
	
	public static long littleEndianToLong(byte[] buffer, int offset) {
		int l = ((buffer[offset + 0] & 0xFF) <<  0) |
		        ((buffer[offset + 1] & 0xFF) <<  8) |
		        ((buffer[offset + 2] & 0xFF) << 16) |
		        ((buffer[offset + 3] & 0xFF) << 24);
		int h = ((buffer[offset + 4] & 0xFF) <<  0) |
		        ((buffer[offset + 5] & 0xFF) <<  8) |
		        ((buffer[offset + 6] & 0xFF) << 16) |
		        ((buffer[offset + 7] & 0xFF) << 24);
		return (((long) h) << 32L) | (((long) l) & 0xFFFFFFFFL);
	}
	
	public static int littleEndianToInt(byte[] buffer, int offset) {
		return (buffer[offset + 0] & 0xFF) | 
		      ((buffer[offset + 1] & 0xFF) <<  8) | 
		      ((buffer[offset + 2] & 0xFF) << 16) | 
		      ((buffer[offset + 3] & 0xFF) << 24);
	}

	public static long littleEndianToIntUnsignedLong(byte[] buffer, int offset) {
		return Integer.toUnsignedLong(littleEndianToInt(buffer, offset));
	}
	
	public static int littleEndianTo24BitUnsignedInt(byte[] buffer, int offset) {
		return (buffer[offset + 0] & 0xFF) | 
		      ((buffer[offset + 1] & 0xFF) <<  8) | 
		      ((buffer[offset + 2] & 0xFF) << 16);
	}
	
	public static int littleEndianTo24BitInt(byte[] buffer, int offset) {
		int val = littleEndianTo24BitUnsignedInt(buffer, offset);
		return ((val & 0x00800000) != 0) ? (val | 0xFF000000) : val;
	}
	
	public static short littleEndianToShort(byte[] buffer, int offset) {
		return (short)((buffer[offset + 0] & 0xFF) | 
		              ((buffer[offset + 1] & 0xFF) << 8));
	}
	
	public static int littleEndianToShortUnsignedInt(byte[] buffer, int offset) {
		return Short.toUnsignedInt(littleEndianToShort(buffer, offset));
	}
	
	public static long bigEndianToLong(byte[] buffer, int offset) {
		int h = ((buffer[offset + 0] & 0xFF) << 24) |
		        ((buffer[offset + 1] & 0xFF) << 16) |
		        ((buffer[offset + 2] & 0xFF) <<  8) |
		        ((buffer[offset + 3] & 0xFF) <<  0);
		int l = ((buffer[offset + 4] & 0xFF) << 24) |
		        ((buffer[offset + 5] & 0xFF) << 16) |
		        ((buffer[offset + 6] & 0xFF) <<  8) |
		        ((buffer[offset + 7] & 0xFF) <<  0);
		return (((long) h) << 32L) | (((long) l) & 0xFFFFFFFFL);
	}
	
	public static int bigEndianToInt(byte[] buffer, int offset) {
		return (buffer[offset + 3] & 0xFF) | 
		      ((buffer[offset + 2] & 0xFF) <<  8) | 
		      ((buffer[offset + 1] & 0xFF) << 16) | 
		      ((buffer[offset + 0] & 0xFF) << 24);
	}
	
	public static long bigEndianToIntUnsignedLong(byte[] buffer, int offset) {
		return Integer.toUnsignedLong(bigEndianToInt(buffer, offset));
	}
	
	public static int bigEndianTo24BitUnsignedInt(byte[] buffer, int offset) {
		return (buffer[offset + 2] & 0xFF) | 
		      ((buffer[offset + 1] & 0xFF) <<  8) | 
		      ((buffer[offset + 0] & 0xFF) << 16);
	}
	
	public static int bigEndianTo24BitInt(byte[] buffer, int offset) {
		int val = bigEndianTo24BitUnsignedInt(buffer, offset);
		return ((val & 0x00800000) != 0) ? (val | 0xFF000000) : val;
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
		buffer[offset + 7] = (byte) ((i >>>  0) & 0xFF);
		buffer[offset + 6] = (byte) ((i >>>  8) & 0xFF);
		buffer[offset + 5] = (byte) ((i >>> 16) & 0xFF);
		buffer[offset + 4] = (byte) ((i >>> 24) & 0xFF);
		
		i = (int) (value >>> 32);
		buffer[offset + 3] = (byte) ((i >>>  0) & 0xFF);
		buffer[offset + 2] = (byte) ((i >>>  8) & 0xFF);
		buffer[offset + 1] = (byte) ((i >>> 16) & 0xFF);
		buffer[offset + 0] = (byte) ((i >>> 24) & 0xFF);
	}
	
	public static void writeBigEndianInt(byte[] buffer, int value, int offset) {
		buffer[offset + 3] = (byte)((value >>>  0) & 0xFF);
		buffer[offset + 2] = (byte)((value >>>  8) & 0xFF);
		buffer[offset + 1] = (byte)((value >>> 16) & 0xFF);
		buffer[offset + 0] = (byte)((value >>> 24) & 0xFF);
	}
	
	public static void writeBigEndian24Bit(byte[] buffer, int value, int offset) {
		buffer[offset + 2] = (byte)((value >>>  0) & 0xFF);
		buffer[offset + 1] = (byte)((value >>>  8) & 0xFF);
		buffer[offset + 0] = (byte)((value >>> 16) & 0xFF);
	}
	
	public static void writeBigEndianShort(byte[] buffer, short value, int offset) {
		buffer[offset + 1] = (byte)((value >>> 0) & 0xFF);
		buffer[offset + 0] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void writeLittleEndianLong(byte[] buffer, long value, int offset) {
		int i = (int) value;
		buffer[offset + 0] = (byte) ((i >>>  0) & 0xFF);
		buffer[offset + 1] = (byte) ((i >>>  8) & 0xFF);
		buffer[offset + 2] = (byte) ((i >>> 16) & 0xFF);
		buffer[offset + 3] = (byte) ((i >>> 24) & 0xFF);
		
		i = (int) (value >>> 32);
		buffer[offset + 4] = (byte) ((i >>>  0) & 0xFF);
		buffer[offset + 5] = (byte) ((i >>>  8) & 0xFF);
		buffer[offset + 6] = (byte) ((i >>> 16) & 0xFF);
		buffer[offset + 7] = (byte) ((i >>> 24) & 0xFF);
	}
	
	public static void writeLittleEndianInt(byte[] buffer, int value, int offset) {
		buffer[offset + 0] = (byte)((value >>>  0) & 0xFF);
		buffer[offset + 1] = (byte)((value >>>  8) & 0xFF);
		buffer[offset + 2] = (byte)((value >>> 16) & 0xFF);
		buffer[offset + 3] = (byte)((value >>> 24) & 0xFF);
	}

	public static void writeLittleEndian24Bit(byte[] buffer, int value, int offset) {
		buffer[offset + 0] = (byte)((value >>>  0) & 0xFF);
		buffer[offset + 1] = (byte)((value >>>  8) & 0xFF);
		buffer[offset + 2] = (byte)((value >>> 16) & 0xFF);
	}
	
	public static void writeLittleEndianShort(byte[] buffer, short value, int offset) {
		buffer[offset + 0] = (byte)((value >>> 0) & 0xFF);
		buffer[offset + 1] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static float bigEndianExtendedToFloat(byte[] buffer, int offset) {
		return (float)bigEndianExtendedToDouble(buffer, offset);
	}
	
	public static double bigEndianExtendedToDouble(byte[] buffer, int offset) {
		// The extended size is 80 bits and can not be converted into
		// an extended type. We will need to convert it to a 64-bit
		// floating point number for it to be supported by Java.
		return convertExtendedToDouble(bigEndianToShortUnsignedInt(buffer, 0), 
		                               bigEndianToIntUnsignedLong(buffer, 2),
		                               bigEndianToIntUnsignedLong(buffer, 6));
	}
	
	public static float littleEndianExtendedToFloat(byte[] buffer, int offset) {
		return (float)littleEndianExtendedToDouble(buffer, offset);
	}
	
	public static double littleEndianExtendedToDouble(byte[] buffer, int offset) {
		return convertExtendedToDouble(littleEndianToShortUnsignedInt(buffer, 8), 
		                               littleEndianToIntUnsignedLong(buffer, 4),
		                               littleEndianToIntUnsignedLong(buffer, 0));
	}
	
	private static double convertExtendedToDouble(int se, long hiMant, long loMant) {
		int expon = se & 0x7FFF;

		double f;
		if (expon == 0 && hiMant == 0L && loMant == 0L) {
			f = 0.0;
		} else {
			if (expon == 0x7FFF) {    /* Infinity or NaN */
				if ((hiMant & 0x40000000L) != 0L || (hiMant & 0xBFFFFFFFL) != 0L)
					return Double.NaN;
				f = Double.POSITIVE_INFINITY;
			} else {
				expon -= EXTENDED_EXPONENT_BIAS;
				f  = MathUtils.scalb((double)hiMant, expon -= 32);
				f += MathUtils.scalb((double)loMant, expon -= 32);
			}
		}

		return ((se & 0x8000) != 0) ? -f : f;
	}
}
