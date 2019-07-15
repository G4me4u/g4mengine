package com.g4mesoft.sound.format;

import javax.sound.sampled.AudioFormat;

import com.g4mesoft.sound.format.info.AudioTag;

public abstract class BasicAudioFile extends AudioFile {

	protected final byte[] data;
	protected final AudioFormat format;
	
	protected AudioTag audioTag;
	
	public BasicAudioFile(byte[] data, AudioFormat format) {
		this.data = data;
		this.format = format;
	
		audioTag = null;
	}

	/**
	 * @return The audio format of the audio data.
	 * 
	 * @see #getData(byte[], int, int, int)
	 */
	@Override
	public AudioFormat getFormat() {
		return format;
	}

	/**
	 * Copies the raw PCM audio data from this audio file into the destination
	 * array.
	 * 
	 * @param dst - The destination array
	 * @param srcPos - The starting position in the raw audio data array.
	 * @param dstPos - The destination start position
	 * @param len - The number of bytes to be copied.
	 * 
	 * @return The amount of bytes actually copied to the destination.
	 * 
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *                                   outside array bounds.
	 * 
	 * @see #getFormat()
	 */
	@Override
	public int getData(byte[] dst, int srcPos, int dstPos, int len) {
		if (len > data.length - srcPos)
			len = data.length - srcPos;
		System.arraycopy(data, srcPos, dst, dstPos, len);
		return len;
	}
	
	/**
	 * Calculates the number of frames in this audio file.
	 * 
	 * @return The number of playable frames stored in this audio file.
	 */
	@Override
	public int getLengthInFrames() {
		return data.length / format.getFrameSize();
	}
	
	@Override
	public void setAudioTag(AudioTag audioTag) {
		this.audioTag = audioTag;
	}
	
	@Override
	public AudioTag getAudioTag() {
		return audioTag;
	}
}
