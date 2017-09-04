package com.g4mesoft.sound.format.info;

import com.g4mesoft.sound.format.info.AudioInfo.AudioInfoType;

public abstract class AudioTag {

	private final String name;
	
	public AudioTag(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public abstract AudioInfo[] getInformation();
	
	public abstract boolean isSupportedInformation(AudioInfoType type);
	
	public abstract AudioInfo getFirstOccuringInformation(AudioInfoType type);
	
	@Override
	public String toString() {
		AudioInfo[] information = getInformation();
		if (information.length > 0) {
			StringBuilder sb = new StringBuilder(getName());
			sb.append(": { ");
			int maxI = information.length;
			int i = 0;
			while(true) {
				AudioInfo info = information[i++];
				sb.append(info.toString());
				if (i >= maxI) return sb.append(" }").toString();
				sb.append(", ");
			}
		}

		return getName();
	}
}
