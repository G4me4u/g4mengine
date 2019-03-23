package com.g4mesoft.sound.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.g4mesoft.math.MathUtils;

public final class AudioHelper {

	private AudioHelper() {
	}
	
	public static String readString(InputStream is, byte[] buffer, int maxLength, int offset) throws IOException {
		return readString(is, buffer, maxLength, offset, StandardCharsets.US_ASCII);
	}

	public static String readString(InputStream is, byte[] buffer, int maxLength, int offset, Charset charset) throws IOException {
		int len = readBytes(is, buffer, maxLength, offset);
		if (len < 0)
			return null;
		if (len == 0)
			return "";
		return toString(buffer, len, offset, charset);
	}
	
	public static int readBytes(InputStream is, byte[] buffer, int maxLength, int offset) throws IOException {
		int numBytes = MathUtils.min(buffer.length - offset, maxLength);
		
		int br = 0;
		while (br < numBytes) {
			int n = is.read(buffer, offset, numBytes - br);
			if (n < 0)
				return br == 0 ? -1 : br;
			br += n;
		}
		
		return br;
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
		return toString(buffer, len, offset, StandardCharsets.US_ASCII);
	}

	public static String toString(byte[] buffer, int len, int offset, Charset charset) {
		return new String(buffer, offset, len, charset);
	}
}
