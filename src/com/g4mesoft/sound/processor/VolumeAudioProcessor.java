package com.g4mesoft.sound.processor;

import com.g4mesoft.math.MathUtils;

public class VolumeAudioProcessor implements IAudioProcessor {

	private float volume;
	
	public VolumeAudioProcessor(float volume) {
		this.volume = volume;
	}

	@Override
	public void process(float[] samples, int numSamples, AudioChannel channel) {
		for (int i = 0; i < numSamples; i++)
			samples[i] = MathUtils.clamp(samples[i] * volume, -1.0f, 1.0f);
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public float getVolume() {
		return volume;
	}
}
