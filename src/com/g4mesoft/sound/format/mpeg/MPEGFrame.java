package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

public class MPEGFrame {

	// Calculated frame data
	public int bitrate;
	public int frequency;
	public int nch;
	
	public int accumulated_size;

	public int crc;
	
	// Layer 1 & 2 decoders
	public MPEGAudioDataLayer1 audioLayer1;
	public MPEGAudioDataLayer2 audioLayer2;

	private MPEGSynthesisSubbandFilter synthesisFilter;

	// Layer 3 decoders
	public MPEGSideInformationLayer3 sideInformation;

	// Common header
	public final MPEGHeader header;
	
	public MPEGFrame() {
		header = new MPEGHeader();

		bitrate = 0;
		frequency = 0;
		nch = 0;
		
		accumulated_size = 0;
		
		crc = 0;
		
		audioLayer1 = null;
		audioLayer2 = null;
		
		synthesisFilter = null;
		
		sideInformation = null;
	}

	public boolean readFrame(MPEGBitStream bitStream) throws IOException {
		while (!readNextFrame(bitStream)) {
			if (header.byteLocation == -1)
				return false;
			bitStream.restoreBytes((int)(bitStream.getBytesRead() - header.byteLocation - 1));
		}
		
		return true;
	}
	
	private boolean readNextFrame(MPEGBitStream bitStream) throws IOException {
		if (!header.readHeader(bitStream))
			return false;
		
		if (header.version != MPEGHeader.MPEG_V10)
			return false; // Currently only supporting MPEG v1
		
		//  bitrate  version layer
		//    1110      1     01   =   1110101 in table
		// NOTE: the bitrate table stores the result in kbits.
		bitrate = MPEGTables.BITRATE_TABLE[header.layer | ((header.version & 0x1) << 2) | (header.bitrate_index << 3)] * 1000;
		// freqency version
		//    00       11   =   0011 in table
		frequency  = MPEGTables.FREQUENCY_TABLE[header.version | (header.sampling_frequency << 2)];

		// NOTE: free format mode not supported
		if (bitrate < 0 || frequency < 0)
			return false;

		if (header.layer == MPEGHeader.LAYER_II && MPEGTables.L2_ALLOWED_MODES_TABLE[header.bitrate_index][header.mode] == 0)
			return false;

		nch = (header.mode == MPEGHeader.SINGLE_CHANNEL) ? 1 : 2;
		
		if (header.layer == MPEGHeader.LAYER_I) {
			accumulated_size = ((12 * bitrate) / frequency + header.padding_bit) << 2; // * 4
		} else {
			accumulated_size = (144 * bitrate) / frequency + header.padding_bit;
		}
		
		if (header.protection_bit == 0)
			readCrc(bitStream);

		// Preload the frame (single read optimization)
		bitStream.prereadBytes(accumulated_size);
		
		switch(header.layer) {
		case MPEGHeader.LAYER_I:
			if (audioLayer1 == null) {
				audioLayer1 = new MPEGAudioDataLayer1();
				if (synthesisFilter == null)
					synthesisFilter = new MPEGSynthesisSubbandFilter();
			}
			if (audioLayer1.readAudioData(bitStream, this, synthesisFilter))
				break;
			return false;
		case MPEGHeader.LAYER_II:
			if (audioLayer2 == null) {
				audioLayer2 = new MPEGAudioDataLayer2();
				if (synthesisFilter == null)
					synthesisFilter = new MPEGSynthesisSubbandFilter();
			}
			if (audioLayer2.readAudioData(bitStream, this, synthesisFilter))
				break;
			return false;
		case MPEGHeader.LAYER_III:
			if (sideInformation == null)
				sideInformation = new MPEGSideInformationLayer3();
			if (sideInformation.readSideInformation(bitStream, this)) {
				bitStream.skip(header.byteLocation + accumulated_size - bitStream.getBytesRead());
				return true;
			}
			return false;
		}
		
		return true;
	}
	
	protected void readCrc(MPEGBitStream bitStream) throws IOException {
		// TODO: implement proper checksum(crc) check.
		crc = bitStream.readBits(16);
	}
}
