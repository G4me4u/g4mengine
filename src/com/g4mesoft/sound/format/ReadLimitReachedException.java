package com.g4mesoft.sound.format;

import java.io.IOException;

@SuppressWarnings("serial")
public class ReadLimitReachedException extends IOException {

	public ReadLimitReachedException() {
		super();
	}

	public ReadLimitReachedException(String msg) {
		super(msg);
	}
}
