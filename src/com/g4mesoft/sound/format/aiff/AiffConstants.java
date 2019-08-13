package com.g4mesoft.sound.format.aiff;

public final class AiffConstants {
	
	/*
	 * There are several data types that we might find of
	 * interest whilst reading the audio file. The size
	 * of these data types are defined below.
	 */
	
	/**
	 * The number of bytes that define a char data type (8 bits)
	 */
	public static final int CHAR_SIZE = 1;

	/**
	 * The number of bytes that define an unsigned char data type (8 bits)
	 */
	public static final int UNSIGNED_CHAR_SIZE = 1;

	/**
	 * The number of bytes that define a short data type (16 bits)
	 */
	public static final int SHORT_SIZE = 2;

	/**
	 * The number of bytes that define an unsigned short data type (16 bits)
	 */
	public static final int UNSIGNED_SHORT_SIZE = 2;

	/**
	 * The number of bytes that define a long data type (32 bits)
	 */
	public static final int LONG_SIZE = 4;
	
	/**
	 * The number of bytes that define an unsigned long data type (32 bits)
	 */
	public static final int UNSIGNED_LONG_SIZE = 4;

	/**
	 * The number of bytes that define an extended float data type (80 bits)
	 */
	public static final int EXTENDED_SIZE = 10;

	/**
	 * The number of bytes that define a ID data type (32 bits)
	 */
	public static final int ID_SIZE = 4;

	/**
	 * The size of the buffer whilst reading the AIFF file. This buffer
	 * has to be at least 10 bytes to fit all the data types.
	 */
	public static final int TEMP_BUFFER_SIZE = 16;
	
	/*
	 * The AIFF audio format contains a main chunk "FORM"
	 * with multiple smaller chunks with different headers
	 * such as COMM and SSND etc.
	 */
	
	/**
	 * FORM chunk indicator at the beginning of an AIFF
	 * file. This AIFF file header contains all the info
	 * needed to decode the audio file
	 */
	public static final int FORM_DEC = 0x464F524D;
	// private static final String FORM = "FORM";
	
	/**
	 * COMM chunk indicator contains all the common information
	 * about the audio file such as the audio sampleSize and
	 * sampleRate.
	 */
	public static final int COMM_DEC = 0x434F4D4D;
	// public static final String COMM = "COMM";
	
	/**
	 * SSND (main audio data) chunk indicator
	 */
	public static final int SSND_DEC = 0x53534E44;
	// private static final String SSND = "SSND";
	
	/**
	 * ID3 tag chunk indicator
	 */
	public static final int ID3_DEC = 0x49443320;
	
	/*
	 * There are different form types in the IFF file
	 * format. This decoder currently only supports the
	 * AIFF form type (Audio IFF file format).
	 */
	
	/**
	 * The AIFF form type as a decimal (ASCII "AIFF")
	 */
	public static final int AIFF_FORM_TYPE = 0x41494646;

	private AiffConstants() {
	}
}
