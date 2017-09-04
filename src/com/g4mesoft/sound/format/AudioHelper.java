package com.g4mesoft.sound.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class AudioHelper {

	private AudioHelper() {
	}
	
	public static String readString(InputStream is, byte[] buffer, int maxLength, int offset) throws IOException {
		return toString(buffer, readBytes(is, buffer, maxLength, offset), offset);
	}

	public static String readString(InputStream is, byte[] buffer, int maxLength, int offset, Charset charset) throws IOException {
		return toString(buffer, readBytes(is, buffer, maxLength, offset), offset, charset);
	}
	
	public static int readBytes(InputStream is, byte[] buffer, int maxLength, int offset) throws IOException {
		return is.read(buffer, offset, Math.min(buffer.length - offset, maxLength));
	}
	
	public static int readByte(InputStream is, byte[] buffer, int offset) throws IOException {
		int b = is.read();
		if (b != -1) {
			buffer[offset] = (byte)b;
			return 1;
		}
		return -1;
	}
	
	public static int readByte(InputStream is) throws IOException {
		return is.read();
	}

	public static String toString(byte[] buffer, int len, int offset) {
		return new String(buffer, offset, len, StandardCharsets.US_ASCII);
	}

	public static String toString(byte[] buffer, int len, int offset, Charset charset) {
		return new String(buffer, offset, len, charset);
	}
	
	public static void writeString(byte[] buffer, String value, int offset, Charset charset) {
		ByteBuffer bb = charset.encode(value);
		bb.get(buffer, offset, bb.capacity());
	}
}
