package com.g4mesoft.sound.format.aiff;

import com.g4mesoft.sound.format.AudioParsingException;

@SuppressWarnings("serial")
public class AiffChunkParsingException extends AudioParsingException {

	public AiffChunkParsingException(String msg) {
		super(msg);
	}

	public AiffChunkParsingException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AiffChunkParsingException(Throwable cause) {
		super(cause);
	}

	public AiffChunkParsingException() {
		super();
	}
}
