package com.g4mesoft.sound.format;

@SuppressWarnings("serial")
public class AudioParsingException extends Exception {

	public AudioParsingException(String msg) {
		super(msg);
	}

	public AudioParsingException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AudioParsingException(Throwable cause) {
		super(cause);
	}

	public AudioParsingException() {
		super();
	}
}
