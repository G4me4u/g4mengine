package com.g4mesoft.sound.format;

import javax.sound.sampled.AudioFormat;

public abstract class AudioFile {

	public AudioFile() {
	}
	
	public abstract AudioFormat getFormat();
	
	/**
	 * Copies the raw PCM audio data in this AudioFile into
	 * the destination array. Note that as an implementer,
	 * one must ensure, that the format of the output matches
	 * the AudioFormat provided by getFormat()
	 * 
	 * @param dst		-	The destination array
	 * @param srcPos	-	The starting position in the
	 * 						raw audio data array.
	 * @param dstPos	-	The destination start position
	 * @param len		-	The number of bytes to be copied.
	 * 
	 * @return  The amount of bytes actually copied to
	 * 			the destination.
	 * @throws IndexOutOfBoundsException If copying would cause 
	 * 									 access of data outside 
	 * 									 array bounds.
	 * 
	 * @see #getData()
	 */
	public abstract int getData(byte[] dst, int srcPos, int dstPos, int len);
	
	public abstract int getLengthInFrames();

	public void dispose() {
	}
}
