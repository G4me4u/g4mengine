package com.g4mesoft.sound.format.info;

public abstract class AudioInfo {

	protected final AudioInfoType type;
	
	public AudioInfo(AudioInfoType type) {
		this.type = type;
	}
	
	public AudioInfoType getType() {
		return type;
	}

	public abstract boolean allowMultiples();
	
	public static abstract class AudioInfoType {
		
		private final String name;
		
		protected AudioInfoType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return getName();
		}
	}

	@Override
	public abstract String toString();
}
