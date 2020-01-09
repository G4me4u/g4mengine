package com.g4mesoft.sound.format.info.id3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.g4mesoft.sound.format.TagParsingException;
import com.g4mesoft.sound.format.info.TextAudioInfo;
import com.g4mesoft.sound.format.info.TextAudioInfo.TextAudioInfoType;

class TextFrameParser extends FrameParser {
	
	private final TextAudioInfoType type;
	
	TextFrameParser(TextAudioInfoType type) {
		this.type = type;
	}
	
	@Override
	public TextAudioInfo loadFrame(InputStream is, int size, byte status, byte format) throws IOException, TagParsingException {
		byte charsetIndex = ID3Helper.readByteSafe(is);
		Charset charset = getCharset(charsetIndex);
		int numOfTerm = getNumOfTermination(charsetIndex);

		List<String> text = new ArrayList<String>();
		
		byte[] buffer = new byte[--size];
		ID3Helper.readBytesSafe(is, buffer, size, 0);

		int p = 0;
		while(p < size) {
			int end = findStringEnd(buffer, size, p, numOfTerm);
			text.add(ID3Helper.toValidatedString(buffer, end - p, p, charset, false));
			p = end + numOfTerm;
		}
		
		return new TextAudioInfo(text.toArray(new String[text.size()]), type);
	}

	@Override
	public boolean isSupported(byte status, byte format) {
		return status == 0 && format == 0;
	}
}
