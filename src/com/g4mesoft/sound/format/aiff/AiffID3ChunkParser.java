package com.g4mesoft.sound.format.aiff;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.TagParsingException;
import com.g4mesoft.sound.format.info.id3.ID3v2Tag;

public class AiffID3ChunkParser implements IAiffChunkParser<AiffID3Chunk> {

	@Override
	public AiffID3Chunk parseChunk(AudioBitInputStream abis, byte[] buffer, int ckID, int ckSize) throws IOException, AiffChunkParsingException {
		long chunkEnd = abis.getBytesRead() + ckSize;
		
		ID3v2Tag tag;
		try {
			tag = ID3v2Tag.loadTag(abis);
		} catch (TagParsingException e) {
			throw new AiffChunkParsingException("AIFF ID3 chunk corrupted", e);
		}
		
		// Some ID3v2 tags are corrupted or
		// may have invalid lengths.
		abis.seekByteLocation(chunkEnd);
		
		if (abis.getBytesRead() != chunkEnd)
			throw new AiffChunkParsingException("AIFF ID3 chunk corrupted");
		
		return new AiffID3Chunk(ckSize, tag);
	}
	
	@Override
	public int getSupportedChunkID() {
		return AiffConstants.ID3_DEC;
	}

}
