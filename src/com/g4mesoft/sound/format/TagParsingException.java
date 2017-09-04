package com.g4mesoft.sound.format;

@SuppressWarnings("serial")
public class TagParsingException extends Exception {

	public TagParsingException(String msg) {
		super(msg);
	}

	public TagParsingException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public TagParsingException(Throwable cause) {
		super(cause);
	}

	public TagParsingException() {
		super();
	}
}
