package com.g4mesoft.sound.format.aiff;

import com.g4mesoft.sound.format.info.id3.ID3v2Tag;

public class AiffID3Chunk implements IAiffChunk {

	private final int chunkSize;
	private final ID3v2Tag tag;
	
	public AiffID3Chunk(int chunkSize, ID3v2Tag tag) {
		this.chunkSize = chunkSize;
		this.tag = tag;
	}
	
	public ID3v2Tag getTag() {
		return tag;
	}
	
	@Override
	public int getChunkID() {
		return AiffConstants.ID3_DEC;
	}

	@Override
	public int getChunkSize() {
		return chunkSize;
	}
}
