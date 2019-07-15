package com.g4mesoft.sound.format.aiff;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.AudioHelper;
import com.g4mesoft.util.MemoryUtil;

public class AiffSSNDChunkParser implements IAiffChunkParser<AiffSSNDChunk> {

	@Override
	public AiffSSNDChunk parseChunk(AudioBitInputStream abis, byte[] buffer, int ckID, int ckSize) throws IOException, AiffChunkParsingException {
		int br = 0;
		
		br += AudioHelper.readBytes(abis, buffer, AiffConstants.LONG_SIZE, 0);
		long offset = MemoryUtil.bigEndianToIntUnsignedLong(buffer, 0);
		br += AudioHelper.readBytes(abis, buffer, AiffConstants.LONG_SIZE, 0);
		long blockSize = MemoryUtil.bigEndianToIntUnsignedLong(buffer, 0);
		
		if (br != AiffConstants.LONG_SIZE + AiffConstants.LONG_SIZE)
			throw new AiffChunkParsingException("AIFF SSND chunk ended abruptly.");

		byte[] soundData = new byte[ckSize - br];
		br += AudioHelper.readBytes(abis, soundData, soundData.length, 0);
		
		int soundDataLength = soundData.length;
		if (br != ckSize) {
			// throw new AiffChunkParsingException("AIFF SSND chunk sound data ended abruptly.");
			
			// It is possible that the SSND chunk
			// ended abruptly because of an IO fault
			// when writing to the file. Fix the
			// audio data by changing the block size
			int missingData = ckSize - br;
			soundDataLength -= missingData;
		}

		return new AiffSSNDChunk(ckSize, offset, blockSize, soundData, soundDataLength);
	}

	@Override
	public int getSupportedChunkID() {
		return AiffConstants.SSND_DEC;
	}
}
