package com.g4mesoft.sound.processor;

public interface IAudioProcessor {

	public abstract void process(float[] samples, int numSamples, AudioChannel channel);
	
}
