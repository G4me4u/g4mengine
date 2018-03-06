package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

public class MPEGAudioDataLayer3 {

	private final MPEGSideInformationLayer3 sideInformation;
	private final MPEGMainDataLayer3 mainData;
	
	public MPEGAudioDataLayer3() {
		sideInformation = new MPEGSideInformationLayer3();
		mainData = new MPEGMainDataLayer3();
	}
	
	public boolean readAudioData(MPEGBitStream bitStream, MPEGFrame frame) throws IOException {
		if (!sideInformation.readSideInformation(bitStream, frame))
			return false;
		
		// Offset by negative main_data_begin (currently not supported)
		if (sideInformation.main_data_begin != 0)
			return false;
		
		if (!mainData.readMainData(bitStream, frame, sideInformation))
			return false;
	
		return true;
	}
}
