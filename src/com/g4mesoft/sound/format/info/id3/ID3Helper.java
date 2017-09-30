package com.g4mesoft.sound.format.info.id3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.g4mesoft.sound.format.AudioHelper;
import com.g4mesoft.sound.format.TagParsingException;

public final class ID3Helper {

	private ID3Helper() {
	}
	
	static String toValidatedString(byte[] buffer, int len, int offset, Charset charset, boolean allowNewline) throws TagParsingException {
		if (charset == StandardCharsets.ISO_8859_1) {
			int maxI = offset + len;
			for (int i = offset; i < maxI; i++) {
				if (buffer[i] < 0x20 && buffer[i] > 0)
					corrupted();
			}
		}

		String s = AudioHelper.toString(buffer, len, offset, charset);
		if (!allowNewline && s.indexOf('\n') >= 0)
			corrupted();
		return s;
	}
	
	static int readByteSafe(InputStream is, byte[] buffer, int offset) throws IOException, TagParsingException {
		if (AudioHelper.readByte(is, buffer, offset) == -1)
			corrupted();
		return 1;
	}
	
	static byte readByteSafe(InputStream is) throws IOException, TagParsingException {
		int b = AudioHelper.readByte(is);
		if (b == -1)
			corrupted();
		return (byte)b;
	}
	
	static int readBytesSafe(InputStream is, byte[] buffer, int maxLength, int offset) throws IOException, TagParsingException {
		if (AudioHelper.readBytes(is, buffer, maxLength, offset) != maxLength)
			corrupted();
		return maxLength;
	}
	
	static int readSynchsafeInt(InputStream is, byte[] buffer) throws IOException, TagParsingException {
		readBytesSafe(is, buffer, 4, 0);
		if (isInvalidSynchsafeInt(buffer))
			corrupted();
		return synchsafeToInt(buffer);
	}

	static void unsupported() throws TagParsingException {
		throw new TagParsingException("Unsupported tag version");
	}

	static void corrupted() throws TagParsingException {
		throw new TagParsingException("Corrupted ID3v2 tag");
	}
	
	static boolean isInvalidSynchsafeInt(byte[] buffer) {
		// Only 7 bits of each byte are used. 
		// The last bit is padded with a zero.
		// With that in mind as we're working 
		// with signed bytes, invalid bytes are 
		// negative.
		return buffer[0] < 0 || buffer[1] < 0 || 
			   buffer[2] < 0 || buffer[3] < 0;
	}
	
	static int synchsafeToInt(byte[] buffer) {
		// Big endian byte order.
		return (buffer[3] & 0xFF) | 
			  ((buffer[2] & 0xFF) << 7 ) |
			  ((buffer[1] & 0xFF) << 14) |
			  ((buffer[0] & 0xFF) << 21);
	}
}
