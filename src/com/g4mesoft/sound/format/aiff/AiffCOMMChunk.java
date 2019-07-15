package com.g4mesoft.sound.format.aiff;

public class AiffCOMMChunk implements IAiffChunk {

	public static final int EXPECTED_CHUNK_SIZE = 18;
	
	private final short numChannels;
	private final long numSampleFrames;
	private final short sampleSize;
	private final float sampleRate;
	
	public AiffCOMMChunk(short numChannels, long numSampleFrames, short sampleSize, float sampleRate) {
		this.numChannels = numChannels;
		this.numSampleFrames = numSampleFrames;
		this.sampleSize = sampleSize;
		this.sampleRate = sampleRate;
	}
	
	public int getChannels() {
		return numChannels;
	}
	
	public long getSampleFrames() {
		return numSampleFrames;
	}
	
	public short getSampleSize() {
		return sampleSize;
	}
	
	public float getSampleRate() {
		return sampleRate;
	}
	
	@Override
	public int getChunkID() {
		return AiffConstants.COMM_DEC;
	}

	@Override
	public int getChunkSize() {
		return EXPECTED_CHUNK_SIZE;
	}
}
