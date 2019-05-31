package com.g4mesoft.sound.processor;

public interface IAudioProcessor {

	public void process(float[] samples, int numSamples, AudioChannel channel);

	public int getSampleLatency();
	
}
