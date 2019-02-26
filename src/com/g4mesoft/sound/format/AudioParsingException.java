package com.g4mesoft.sound.format;

import java.io.IOException;

@SuppressWarnings("serial")
public class AudioParsingException extends IOException {

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
