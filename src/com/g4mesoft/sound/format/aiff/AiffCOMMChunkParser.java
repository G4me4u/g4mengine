package com.g4mesoft.sound.format.aiff;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.AudioHelper;
import com.g4mesoft.util.MemoryUtil;

public class AiffCOMMChunkParser implements IAiffChunkParser<AiffCOMMChunk> {

	@Override
	public AiffCOMMChunk parseChunk(AudioBitInputStream abis, byte[] buffer, int ckID, int ckSize) throws IOException, AiffChunkParsingException {
		int br = 0;
		
		br += AudioHelper.readBytes(abis, buffer, AiffConstants.SHORT_SIZE, 0);
		short numChannels = MemoryUtil.bigEndianToShort(buffer, 0);
		br += AudioHelper.readBytes(abis, buffer, AiffConstants.UNSIGNED_LONG_SIZE, 0);
		long numSampleFrames = MemoryUtil.bigEndianToIntUnsignedLong(buffer, 0);
		br += AudioHelper.readBytes(abis, buffer, AiffConstants.SHORT_SIZE, 0);
		short sampleSize = MemoryUtil.bigEndianToShort(buffer, 0);
		br += AudioHelper.readBytes(abis, buffer, AiffConstants.EXTENDED_SIZE, 0);
		float sampleRate = MemoryUtil.bigEndianExtendedToFloat(buffer, 0);
		
		if (br != AiffCOMMChunk.EXPECTED_CHUNK_SIZE)
			throw new AiffChunkParsingException("AIFF COMM chunk ended abruptly.");
		if (sampleSize < 1 || sampleSize > 32)
			throw new AiffChunkParsingException("Invalid sample size in AIFF COMM chunk");
		
		return new AiffCOMMChunk(numChannels, numSampleFrames, sampleSize, sampleRate);
	}

	@Override
	public int getSupportedChunkID() {
		return AiffConstants.COMM_DEC;
	}
}
