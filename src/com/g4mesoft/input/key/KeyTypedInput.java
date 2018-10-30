package com.g4mesoft.input.key;

import com.g4mesoft.input.Input;

public class KeyTypedInput extends Input {

	public static final int DEFAULT_BUFFER_SIZE = 4;
	
	private final char[] buffer;
	private int bufferPos;

	private boolean recording;
	private boolean recordNext;
	
	public KeyTypedInput() {
		this(DEFAULT_BUFFER_SIZE);
	}
	
	public KeyTypedInput(int bufferCapacity) {
		buffer = new char[bufferCapacity];
		bufferPos = 0;
	}

	public void keyTyped(char c) {
		synchronized (buffer) {
			if (!recording || bufferPos >= buffer.length)
				return;
			
			buffer[bufferPos++] = c;
		}
	}
	
	public void update() {
		if (recording || recordNext) {
			synchronized (buffer) {
				recording = recordNext;
				recordNext = false;
				bufferPos = 0;
			}
		}
	}
	
	public void reset() {
		synchronized (buffer) {
			bufferPos = 0;
			recording = false;
		}

		recordNext = false;
	}
	
	public char[] getTypedChars() {
		synchronized (buffer) {
			recordNext = true;

			char[] dest = new char[bufferPos];
			if (bufferPos > 0)
				System.arraycopy(buffer, 0, dest, 0, bufferPos);
			return dest;
		}
		
	}
	
	public boolean isRecording() {
		return recording;
	}
	
	@Override
	public boolean isActive() {
		return recording;
	}

	@Override
	public long getActivationTime() {
		return -1;
	}
}
