package com.g4mesoft.sound.format.info.id3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.g4mesoft.sound.format.TagParsingException;
import com.g4mesoft.sound.format.info.TextAudioInfo;
import com.g4mesoft.sound.format.info.TextAudioInfo.TextAudioInfoType;

class URLFrameParser extends FrameParser {
	
	private final TextAudioInfoType type;
	
	URLFrameParser(TextAudioInfoType type) {
		this.type = type;
	}
	
	@Override
	public TextAudioInfo loadFrame(InputStream is, int size, byte status, byte format) throws IOException, TagParsingException {
		byte[] buffer = new byte[size];
		ID3Helper.readBytesSafe(is, buffer, size, 0);

		int end = findStringEnd(buffer, size, 0, 1);
		String url = ID3Helper.toValidatedString(buffer, end, 0, StandardCharsets.ISO_8859_1, false);
		return new TextAudioInfo(new String[]{ url }, type);
	}

	@Override
	public boolean isSupported(byte status, byte format) {
		return status == 0 && format == 0;
	}
}