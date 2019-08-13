package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;

public class MPEGAudioDataLayer3 implements IMPEGAudioData {

	private final MPEGSideInformationLayer3 sideInformation;
	private final MPEGMainDataLayer3 mainData;
	
	public MPEGAudioDataLayer3() {
		sideInformation = new MPEGSideInformationLayer3();
		mainData = new MPEGMainDataLayer3();
	}
	
	@Override
	public void readAudioData(AudioBitInputStream abis, MPEGFrame frame) throws IOException, CorruptedMPEGFrameException {
		sideInformation.readSideInformation(abis, frame);

		// Offset by negative main_data_begin (currently not supported)
		if (sideInformation.main_data_begin != 0)
			throw new CorruptedMPEGFrameException("Unsupported main data pointer");
			
		mainData.readMainData(abis, frame, sideInformation);
	}

	@Override
	public void silence() {
	}
	
	@Override
	public float[] getSamples() {
		return new float[0];
	}

	@Override
	public int getNumSamples() {
		return 0;
	}
	
	@Override
	public int getSupportedLayer() {
		return MPEGHeader.LAYER_III;
	}
}
