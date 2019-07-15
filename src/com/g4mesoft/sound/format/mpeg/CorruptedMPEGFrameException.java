package com.g4mesoft.sound.format.mpeg;

import com.g4mesoft.sound.format.AudioParsingException;

@SuppressWarnings("serial")
public class CorruptedMPEGFrameException extends AudioParsingException {

	public CorruptedMPEGFrameException(String msg) {
		super(msg);
	}

	public CorruptedMPEGFrameException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CorruptedMPEGFrameException(Throwable cause) {
		super(cause);
	}

	public CorruptedMPEGFrameException() {
		super();
	}
}
