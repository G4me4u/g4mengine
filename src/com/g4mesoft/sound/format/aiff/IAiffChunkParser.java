package com.g4mesoft.sound.format.aiff;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;

public interface IAiffChunkParser<T extends IAiffChunk> {

	public T parseChunk(AudioBitInputStream abis, byte[] buffer, int ckID, int ckSize) throws IOException, AiffChunkParsingException;

	public int getSupportedChunkID();

}
