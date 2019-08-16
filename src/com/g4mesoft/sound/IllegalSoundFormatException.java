package com.g4mesoft.sound;

@SuppressWarnings("serial")
public class IllegalSoundFormatException extends RuntimeException {

	public IllegalSoundFormatException(String msg) {
		super(msg);
	}

	public IllegalSoundFormatException() {
		super();
	}
}
