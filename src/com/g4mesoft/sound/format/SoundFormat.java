package com.g4mesoft.sound.format;

public class SoundFormat {

	private final SoundEncoding encoding;
	
	private final float sampleRate;

	private final int bitsPerSample;
	private final boolean leftShifted;
	private final boolean bigEndian;
	
	private final int frameSize;
	private final int channels;

	public SoundFormat(SoundEncoding encoding, float sampleRate, int bitsPerSample, 
			boolean leftShifted, boolean bigEndian, int frameSize, int channels) {
		
		this.encoding = encoding;
		
		this.sampleRate = sampleRate;
	
		this.bitsPerSample = bitsPerSample;
		this.leftShifted = leftShifted;
		this.bigEndian = bigEndian;
		
		this.frameSize = frameSize;
		this.channels = channels;
	}
	
	public SoundEncoding getEncoding() {
		return encoding;
	}
	
	public float getSampleRate() {
		return sampleRate;
	}
	
	public int getBitsPerSample() {
		return bitsPerSample;
	}
	
	public boolean isLeftShifted() {
		return leftShifted;
	}
	
	public boolean isBigEndian() {
		return bigEndian;
	}
	
	public int getFrameSize() {
		return frameSize;
	}
	
	public int getChannels() {
		return channels;
	}
}
