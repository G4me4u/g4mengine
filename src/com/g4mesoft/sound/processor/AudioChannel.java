package com.g4mesoft.sound.processor;

public enum AudioChannel {
	
	LEFT(0, "left"), 
	RIGHT(1, "right");
	
	public final int index;
	public final String desc;
	
	AudioChannel(int index, String desc) {
		this.index = index;
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return desc;
	}
}
