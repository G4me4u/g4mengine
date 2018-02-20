package com.g4mesoft.sound.processor;

public class VolumeAudioProcessor implements AudioProcessor {

	private float volume;
	
	public VolumeAudioProcessor(float volume) {
		this.volume = volume;
	}

	@Override
	public void process(float[] samples, int numSamples, AudioChannel channel) {
		float s;
		for (int i = 0; i < numSamples; i++) {
			s = samples[i] * volume;
			
			if (s > 1.0f) {
				samples[i] =  1.0f;
				continue;
			}
			
			if (s < -1.0f) {
				samples[i] = -1.0f;
				continue;
			}

			samples[i] = s;
		}
	}

	public void setVolume(float nVolume) {
		volume = nVolume;
	}

	public float getVolume() {
		return volume;
	}
}
