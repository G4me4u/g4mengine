package com.g4mesoft.sound.processor;

import java.util.LinkedList;
import java.util.List;

import com.g4mesoft.math.Vec3f;

public abstract class AudioLine {

	protected final List<IAudioProcessor> preProcessors;
	
	protected final Vec3f pos;
	
	public AudioLine(Vec3f pos) {
		this.pos = new Vec3f(pos);
		
		preProcessors = new LinkedList<IAudioProcessor>();
	}

	public void addPreProcessor(IAudioProcessor preAudioProcessor) {
		synchronized (preProcessors) {
			preProcessors.add(preAudioProcessor);
		}
	}
	
	public void preProcess(float[] samples, int numSamples, AudioChannel channel) {
		synchronized (preProcessors) {
			for (IAudioProcessor audioProcessor : preProcessors)
				audioProcessor.process(samples, numSamples, channel);
		}
	}

	public boolean hasPreProcessors() {
		synchronized (preProcessors) {
			return !preProcessors.isEmpty();
		}
	}
	
	public void setPosition(Vec3f nPos) {
		pos.set(nPos);
	}
	
	public Vec3f getPosition() {
		return pos;
	}
}
