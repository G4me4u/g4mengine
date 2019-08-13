package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;

public interface IMPEGAudioData {

	public void readAudioData(AudioBitInputStream abis, MPEGFrame frame) throws IOException, CorruptedMPEGFrameException;

	public void silence();
	
	public float[] getSamples();

	public int getNumSamples();
	
	public int getSupportedLayer();
	
}
