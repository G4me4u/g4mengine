package com.g4mesoft.sound.format.info.id3;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import com.g4mesoft.sound.format.TagParsingException;
import com.g4mesoft.sound.format.info.PictureAudioInfo;
import com.g4mesoft.sound.format.info.PictureAudioInfo.PictureAudioInfoType;
import com.g4mesoft.sound.format.info.PictureAudioInfo.PictureType;

class PictureFrameParser extends FrameParser {
	
	private static final String URL_MIME = "-->";
	
	private final PictureAudioInfoType type;
	
	PictureFrameParser(PictureAudioInfoType type) {
		this.type = type;
	}
	
	@Override
	public PictureAudioInfo loadFrame(InputStream is, int size, byte status, byte format) throws IOException, TagParsingException {
		byte charsetIndex = ID3Helper.readByteSafe(is);
		Charset charset = getCharset(charsetIndex);
		int numOfTerm = getNumOfTermination(charsetIndex);

		byte[] buffer = readStringAsBytesSafe(is, 16, 1);
		String mime = ID3Helper.toValidatedString(buffer, buffer.length - 1, 0, StandardCharsets.ISO_8859_1, false);
		size -= buffer.length;
		// Delete buffer, before re-allocating
		// memory later.
		buffer = null;
		
		PictureType picType = PictureType.fromID(ID3Helper.readByteSafe(is));
		if (picType == null)
			corrupted();
		size--;
		
		buffer = readStringAsBytesSafe(is, 16, numOfTerm);
		String desc = ID3Helper.toValidatedString(buffer, buffer.length - numOfTerm, 0, charset, false);
		size -= buffer.length;
		buffer = null;
		
		if (URL_MIME.equals(mime)) {
			buffer = new byte[size];
			ID3Helper.readBytesSafe(is, buffer, size, 0); // Read the rest of bytes
			String url = ID3Helper.toValidatedString(buffer, size, 0, StandardCharsets.ISO_8859_1, false);
			return new PictureAudioInfo(picType, desc, url, type);
		}
		
		// There's no way to make sure we're
		// reading the right amount of bytes..
		// we're assuming, that if this frame
		// has an invalid size, that it will
		// throw an Exception later on.
		return new PictureAudioInfo(picType, desc, readImage(is, mime), type);
	}
	
	private static BufferedImage readImage(InputStream is, String mime) throws IOException, TagParsingException {
		Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(mime);
		if (!readers.hasNext())
			corrupted();
		ImageReader reader = readers.next();

		reader.setInput(ImageIO.createImageInputStream(is), true, true);
		
		BufferedImage bi = null;
		try {
			bi = reader.read(0, reader.getDefaultReadParam());
		} catch (IndexOutOfBoundsException | IllegalArgumentException | IIOException e) {
			corrupted();
		} finally {
			reader.dispose();
		}
		
		if (bi == null)
			corrupted();
		
		return bi;
	}
	
	@Override
	public boolean isSupported(byte status, byte format) {
		return status == 0 && format == 0;
	}
}
