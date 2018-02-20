package com.g4mesoft.sound.processor;

import java.util.ArrayList;
import java.util.List;

import com.g4mesoft.math.Vec3f;

public abstract class AudioLine {

	protected final List<AudioProcessor> preProcessors;
	
	protected final Vec3f pos;
	
	public AudioLine(Vec3f pos) {
		this.pos = new Vec3f(pos);
		
		preProcessors = new ArrayList<AudioProcessor>();
	}

	public void addPreProcessor(AudioProcessor preAudioProcessor) {
		preProcessors.add(preAudioProcessor);
	}
	
	public void preProcess(float[] samples, int numSamples, AudioChannel channel) {
		for (AudioProcessor audioProcessor : preProcessors)
			audioProcessor.process(samples, numSamples, channel);
	}

	public boolean hasPreProcessors() {
		return !preProcessors.isEmpty();
	}
	
	public void setPosition(Vec3f nPos) {
		pos.set(nPos);
	}
	
	public Vec3f getPosition() {
		return pos;
	}
}
