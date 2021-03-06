package com.g4mesoft.sound.format.aiff;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.SoundEncoding;
import com.g4mesoft.sound.format.SoundFormat;
import com.g4mesoft.sound.format.AudioHelper;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.BasicAudioFile;
import com.g4mesoft.util.MemoryUtil;

public class AiffFile extends BasicAudioFile {

	/**
	 * The supported chunk parsers of this AIFF file
	 */
	private static Map<Integer, IAiffChunkParser<? extends IAiffChunk>> supportedChunkParsers;
	
	/**
	 * The maximum search depth for the FORM ID (1 kB)
	 */
	private static final int ID_SEARCH_DEPTH = 1024;
	
	private AiffFile(byte[] data, SoundFormat format) {
		super(data, format);
	}

	public static AiffFile loadAIFF(AudioBitInputStream abis) throws IOException, AudioParsingException {
		// Search for FORM marker
		if (!abis.findBytePattern(AiffConstants.FORM_DEC, AiffConstants.ID_SIZE, ID_SEARCH_DEPTH))
			return null;
		
		// Buffer has to be big enough to contain
		// all the data types.
		byte[] buffer = new byte[AiffConstants.TEMP_BUFFER_SIZE];

		AudioHelper.readBytes(abis, buffer, AiffConstants.LONG_SIZE, 0);
		int formSize = MemoryUtil.bigEndianToInt(buffer, 0);
		if (formSize <= 0 || abis.isEndOfStream())
			return null;
		
		// chunkEnd = (padding + ID) + long + formSize
		long chunkEnd = abis.getBytesRead() + formSize;
		
		// The data portion starts after the size
		// definition. End of file will be handled
		// by later calls to readBytes.
		AudioHelper.readBytes(abis, buffer, AiffConstants.ID_SIZE, 0);
		int formType = MemoryUtil.bigEndianToInt(buffer, 0);
		
		// We only support AIFF form type
		if (formType != AiffConstants.AIFF_FORM_TYPE || abis.isEndOfStream())
			return null;

		// Invalidate the read limit. This means
		// there's no protection beyond this point
		// If this file is not a valid AIFF file
		// it will likely cause issues beyond
		// this point.
		abis.invalidateReadLimit();
		
		if (supportedChunkParsers == null)
			initializeChunkParsers();
		
		// Read chunks
		Map<Integer, IAiffChunk> chunks = new HashMap<Integer, IAiffChunk>();
		
		while (abis.getBytesRead() < chunkEnd) {
			if (!readAiffChunk(abis, buffer, chunks)) {
				// We might have had an invalidly sized
				// chunk inside our AIFF file. This could
				// cause a corruption, but will most
				// likely be fine. Continue and check the
				// damage later.
				break;
			}
		}
		
		AiffCOMMChunk commChunk = (AiffCOMMChunk)chunks.get(AiffConstants.COMM_DEC);
		AiffSSNDChunk ssndChunk = (AiffSSNDChunk)chunks.get(AiffConstants.SSND_DEC);
		if (commChunk == null || ssndChunk == null)
			return null;
		
		int frameSize = (commChunk.getSampleSize() + 7) / 8 * commChunk.getChannels();
		SoundFormat format = new SoundFormat(SoundEncoding.PCM_SIGNED,
		                                     commChunk.getSampleRate(),
		                                     commChunk.getSampleSize(),
		                                     false,
		                                     true,
		                                     frameSize,
		                                     commChunk.getChannels());

		byte[] data = extractSoundDataFromChunks(ssndChunk, commChunk, format);
		
		AiffFile audioFile = new AiffFile(data, format);
		
		// Set audio tag
		AiffID3Chunk id3Chunk = (AiffID3Chunk)chunks.get(AiffConstants.ID3_DEC);
		if (id3Chunk != null)
			audioFile.setAudioTag(id3Chunk.getTag());
		
		return audioFile;
	}
	
	private static boolean readAiffChunk(AudioBitInputStream abis, byte[] buffer, Map<Integer, IAiffChunk> chunks) throws IOException {
		AudioHelper.readBytes(abis, buffer, AiffConstants.ID_SIZE, 0);
		int ckID = MemoryUtil.bigEndianToInt(buffer, 0);
		
		AudioHelper.readBytes(abis, buffer, AiffConstants.LONG_SIZE, 0);
		int ckSize = MemoryUtil.bigEndianToInt(buffer, 0);
		
		if (ckSize < 0 || abis.isEndOfStream())
			return false;

		IAiffChunkParser<? extends IAiffChunk> parser = supportedChunkParsers.get(ckID);
		if (parser != null && !chunks.containsKey(ckID)) {
			IAiffChunk chunk;
			try {
				chunk = parser.parseChunk(abis, buffer, ckID, ckSize);
			} catch (AiffChunkParsingException e) {
				// The audio file is corrupted.
				return false;
			}
			
			chunks.put(chunk.getChunkID(), chunk);
		} else {
			// We don't support the current chunk.
			abis.skip(ckSize);
		}
		
		return true;
	}

	private static byte[] extractSoundDataFromChunks(AiffSSNDChunk ssndChunk, AiffCOMMChunk commChunk, SoundFormat format) {
		int numDataBytes = (int)commChunk.getSampleFrames() * format.getFrameSize();
		
		int offset = (int)ssndChunk.getOffset();
		byte[] soundData = ssndChunk.getSoundData();

		// It is possible that the SSND chunk was
		// corrupted and ended abruptly. We have
		// to make sure that the data is actually
		// present.
		if (ssndChunk.getSoundDataLength() < numDataBytes + offset) {
			numDataBytes = ssndChunk.getSoundDataLength() - offset;
		
			// We have to make sure that the last
			// frame is not cut off or broken.
			numDataBytes -= numDataBytes % format.getFrameSize();
		}

		if (numDataBytes < 0 || offset > soundData.length)
			return new byte[0];
		if (offset == 0 && soundData.length == numDataBytes)
			return soundData;
		
		if (offset + numDataBytes > soundData.length)
			numDataBytes = soundData.length - offset;
		byte[] data = new byte[numDataBytes];
		System.arraycopy(soundData, offset, data, 0, numDataBytes);

		return data;
	}
	
	private static void initializeChunkParsers() {
		if (supportedChunkParsers != null)
			return;
		
		supportedChunkParsers = new HashMap<Integer, IAiffChunkParser<? extends IAiffChunk>>();
		
		addSupportedChunkParser(new AiffCOMMChunkParser());
		addSupportedChunkParser(new AiffSSNDChunkParser());
		addSupportedChunkParser(new AiffID3ChunkParser());
	}

	private static void addSupportedChunkParser(IAiffChunkParser<?> chunkParser) {
		supportedChunkParsers.put(chunkParser.getSupportedChunkID(), chunkParser);
	}
}
