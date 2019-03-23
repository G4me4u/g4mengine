package com.g4mesoft.sound.format.info.id3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.g4mesoft.math.MathUtils;
import com.g4mesoft.sound.format.TagParsingException;
import com.g4mesoft.sound.format.info.AudioInfo;

abstract class FrameParser {
	
	protected static final byte TERMINATION_BYTE = 0x00;
	
	protected static final int ISO_8859_1 = 0x00;
	protected static final int UTF_16 = 0x01;
	protected static final int UTF_16BE = 0x02;
	protected static final int UTF_8 = 0x03;
	
	protected FrameParser() {
	}
	
	public abstract AudioInfo loadFrame(InputStream is, int size, byte status, byte format) throws IOException, TagParsingException;

	public abstract boolean isSupported(byte status, byte format);
	
	protected static void unsupported() throws TagParsingException {
		throw new TagParsingException("Unsupported ID3v2 frame");
	}

	protected static void corrupted() throws TagParsingException {
		throw new TagParsingException("Corrupted ID3v2 frame");
	}
	
	protected Charset getCharset(byte charsetIndex) throws TagParsingException {
		switch(charsetIndex) {
		case ISO_8859_1:
			return StandardCharsets.ISO_8859_1;
		case UTF_8:
			return StandardCharsets.UTF_8;
		case UTF_16:
			return StandardCharsets.UTF_16;
		case UTF_16BE:
			return StandardCharsets.UTF_16BE;
		default:
			corrupted();
			return null;
		}
	}

	protected int getNumOfTermination(byte charsetIndex) throws TagParsingException {
		switch(charsetIndex) {
		case ISO_8859_1:
		case UTF_8:
			return 1;
		case UTF_16:
		case UTF_16BE:
			return 2;
		default:
			corrupted();
			return -1;
		}
	}
	
	protected static int findStringEnd(byte[] buffer, int maxLength, int offset, int numOfTerm) throws TagParsingException {
		int end = MathUtils.min(buffer.length, maxLength + offset);
		
		int tc = 0;
		while(offset < end) {
			byte b = buffer[offset++];
			if (b == TERMINATION_BYTE) {
				if (++tc >= numOfTerm) 
					return offset - tc;
			} else tc = 0;
		}

		// Not null terminated
		return offset;
	}
	
	protected static byte[] readStringAsBytesSafe(InputStream is, int initialBufferCap, int numOfTerm) throws IOException, TagParsingException {
		byte[] buffer = new byte[initialBufferCap];
		
		int p = 0;
		int tc = 0;
		while(true) {
			byte b = ID3Helper.readByteSafe(is);

			if (p > buffer.length)
				buffer = Arrays.copyOf(buffer, buffer.length * 2);
			
			buffer[p++] = (byte)b;
			if ((byte)b == TERMINATION_BYTE) {
				if (++tc >= numOfTerm)
					break;
			} else tc = 0;
		}
		
		if (p != buffer.length)
			return Arrays.copyOf(buffer, p);
		return buffer;
	}
}