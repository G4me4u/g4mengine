package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;

public class MPEGFrame {

	private static final int CRC_BIT_SIZE = 16;
	
	private final MPEGFrameDecoder frameDecoder;
	
	private final MPEGHeader header;
	private IMPEGAudioData audioData;
	public int crc;

	public int bitrate;
	public int frequency;
	public int nch;
	public int accumulatedSize;
	
	public MPEGFrame(MPEGFrameDecoder frameDecoder) {
		this.frameDecoder = frameDecoder;
		
		header = new MPEGHeader();
		audioData = null;
	}
	
	public boolean readFrameHeader(AudioBitInputStream abis) throws IOException {
		do {
			while (!header.readHeader(abis)) {
				if (abis.isEndOfStream())
					return false;
				
				// Header is invalid and we're not at the end
				// of the stream. Restore the frame with an
				// offset of 1. This ensures that the frame
				// will not be the same as the previous.
				restoreFrame(abis, 1);
			}
			
			if (header.protection_bit == 0)
				readCRC(abis);
	
			// Convert from kbits per second to bits per second.
			bitrate = MPEGTables.getBitrate(header.bitrate_index, header.version, header.layer) * 1000;
			frequency = MPEGTables.getFrequency(header.sampling_frequency, header.version);
			nch = (header.mode == MPEGHeader.SINGLE_CHANNEL) ? 1 : 2;
	
			if (header.layer == MPEGHeader.LAYER_I) {
				accumulatedSize = ((12 * bitrate) / frequency + header.padding_bit) << 2; // * 4
			} else {
				accumulatedSize = (144 * bitrate) / frequency + header.padding_bit;
			}
		} while (!isValidHeader());
	
		return true;
	}
	
	public void readAudioData(AudioBitInputStream abis) throws IOException, CorruptedMPEGFrameException {
		IMPEGAudioData audioData = this.audioData;
		if (audioData == null)
			audioData = getAppropriateDecoder();
	
		if (audioData.getSupportedLayer() != header.layer)
			throw new CorruptedMPEGFrameException("MPEG layer changed");
		audioData.readAudioData(abis, this);
		
		this.audioData = audioData;
	}
	
	public void restoreFrame(AudioBitInputStream abis, int offset) throws IOException {
		if (header.byteLocation == -1L)
			return;
		
		abis.seekByteLocation(header.byteLocation + offset);
	}
	
	private void readCRC(AudioBitInputStream abis) throws IOException {
		crc = abis.readBits(CRC_BIT_SIZE);
	}
	
	private boolean isValidHeader() {
		if (header.version != MPEGHeader.MPEG_V10)
			return false; // Currently only supporting MPEG v1

		// NOTE: free format mode not supported
		if (bitrate <= 0 || frequency == -1)
			return false;
		
		if (header.layer == MPEGHeader.LAYER_II)
			return MPEGTables.L2_ALLOWED_MODES_TABLE[header.bitrate_index][header.mode] != 0;
		
		return true;
	}

	private IMPEGAudioData getAppropriateDecoder() {
		switch(header.layer) {
		case MPEGHeader.LAYER_I:
			return new MPEGAudioDataLayer1();
		case MPEGHeader.LAYER_II:
			return new MPEGAudioDataLayer2();
		case MPEGHeader.LAYER_III:
			return new MPEGAudioDataLayer3();
		}
		
		return null;
	}
	
	public MPEGHeader getHeader() {
		return header;
	}

	public int getNumSamples() {
		return (audioData == null) ? 0 : audioData.getSamples().length;
	}
	
	public float[] getSamples() {
		return (audioData == null) ? null : audioData.getSamples();
	}

	public MPEGSynthesisSubbandFilter getSynthesisSubbandFilter() {
		return frameDecoder.getSynthesisSubbandFilter();
	}
}
