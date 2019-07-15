package com.g4mesoft.sound.format.aiff;

public class AiffSSNDChunk implements IAiffChunk {

	private final int chunkSize;
	private final long offset;
	private final long blockSize;
	
	private final byte[] soundData;
	private final int soundDataLength;
	
	public AiffSSNDChunk(int chunkSize, long offset, long blockSize, byte[] soundData, int soundDataLength) {
		this.chunkSize = chunkSize;

		this.offset = offset;
		this.blockSize = blockSize;
		this.soundData = soundData;
		this.soundDataLength = soundDataLength;
	}
	
	public long getOffset() {
		return offset;
	}
	
	public long getBlockSize() {
		return blockSize;
	}
	
	public byte[] getSoundData() {
		return soundData;
	}
	
	public int getSoundDataLength() {
		return soundDataLength;
	}
	
	@Override
	public int getChunkID() {
		return AiffConstants.SSND_DEC;
	}

	@Override
	public int getChunkSize() {
		return chunkSize;
	}
}
