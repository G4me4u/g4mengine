package com.g4mesoft.sound.format.info.id3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.g4mesoft.sound.format.TagParsingException;
import com.g4mesoft.sound.format.info.AudioInfo;
import com.g4mesoft.sound.format.info.PictureAudioInfo.PictureAudioInfoType;
import com.g4mesoft.sound.format.info.TextAudioInfo.TextAudioInfoType;

public final class FrameParserManager {

	private FrameParserManager() {
	}
	
	private static final Map<String, FrameParser> PARSERS = new HashMap<String, FrameParser>();
	
	public static int readFrame(InputStream is, byte[] buffer, List<AudioInfo> information, boolean allowPadding) throws IOException, TagParsingException {
		if (allowPadding) {
			ID3Helper.readByteSafe(is, buffer, 0);
			if (buffer[0] == ID3v2Tag.PADDING_BYTE) // Padding
				return 1;
			ID3Helper.readBytesSafe(is, buffer, 3, 1); // Read last 3 bytes (no padding here)
		} else ID3Helper.readBytesSafe(is, buffer, 4, 0);
		
		String id = ID3Helper.toValidatedString(buffer, 4, 0, StandardCharsets.ISO_8859_1, false);
		int frameSize = ID3Helper.readSynchsafeInt(is, buffer);
		
		ID3Helper.readBytesSafe(is, buffer, 2, 0);
		byte status = buffer[0];
		byte format = buffer[1];

		FrameParser parser = getParser(id);
		
		if (parser == null || !parser.isSupported(status, format)) {
			if (is.skip(frameSize) != frameSize)
				ID3Helper.corrupted();
		} else {
			information.add(parser.loadFrame(is, frameSize, status, format));
		}
		
		return frameSize + 10;
	}
	
	public static boolean isSupportedFrame(String id) {
		return PARSERS.containsKey(id);
	}
	
	public static FrameParser getParser(String id) {
		return PARSERS.get(id);
	}
	
	private static void addParser(String id, FrameParser parser) {
		if (isSupportedFrame(id))
			throw new IllegalArgumentException("Duplicate id " + id);
		PARSERS.put(id, parser);
	}
	
	private static void addPicParser(String id, PictureAudioInfoType type) {
		addParser(id, new PictureFrameParser(type));
	}

	private static void addTextParser(String id, TextAudioInfoType type) {
		addParser(id, new TextFrameParser(type));
	}
	
	private static void addURLParser(String id, TextAudioInfoType type) {
		addParser(id, new URLFrameParser(type));
	}
	
	private static void loadParsers() {
		if (!PARSERS.isEmpty())
			PARSERS.clear();
		
		addPicParser("APIC", PictureAudioInfoType.ATTACHED_PICTURE);
		
		addTextParser("TALB", TextAudioInfoType.ALBUM_TITLE);
		addTextParser("TBPM", TextAudioInfoType.BEATS_PER_MINUTE);
		addTextParser("TCOM", TextAudioInfoType.COMPOSER);
		addTextParser("TCON", TextAudioInfoType.CONTENT_TYPE);
		addTextParser("TCOP", TextAudioInfoType.COPYRIGHT_MESSSAGE);
		addTextParser("TDEN", TextAudioInfoType.ENCODING_TIME);
		addTextParser("TDLY", TextAudioInfoType.PLAYLIST_DELAY);
		addTextParser("TDOR", TextAudioInfoType.ORIGINAL_RELEASE_TIME);
		addTextParser("TDRC", TextAudioInfoType.RECORDING_TIME);
		addTextParser("TDRL", TextAudioInfoType.RELEASE_TIME);
		addTextParser("TDTG", TextAudioInfoType.TAGGING_TIME);
		addTextParser("TENC", TextAudioInfoType.ENCODED_BY);
		addTextParser("TEXT", TextAudioInfoType.LYRICIST);
		addTextParser("TFLT", TextAudioInfoType.FILE_TYPE);
		addTextParser("TIPL", TextAudioInfoType.INVOLVED_PEOPLE);
		addTextParser("TIT1", TextAudioInfoType.CONTENT_GROUP_DESCRIPTION);
		addTextParser("TIT2", TextAudioInfoType.TITLE);
		addTextParser("TIT3", TextAudioInfoType.SUBTITLE);
		addTextParser("TKEY", TextAudioInfoType.INITIAL_KEY);
		addTextParser("TLAN", TextAudioInfoType.LANGUAGE);
		addTextParser("TLEN", TextAudioInfoType.LENGTH);
		addTextParser("TMCL", TextAudioInfoType.MUSICIAN_CREDITS_LIST);
		addTextParser("TMED", TextAudioInfoType.MEDIA_TYPE);
		addTextParser("TMOO", TextAudioInfoType.MOOD);
		addTextParser("TOAL", TextAudioInfoType.ORIGINAL_ALBUM_TITLE);
		addTextParser("TOFN", TextAudioInfoType.ORIGINAL_FILENAME);
		addTextParser("TOLY", TextAudioInfoType.ORIGINAL_LYRICIST);
		addTextParser("TOPE", TextAudioInfoType.ORIGINAL_ARTIST);
		addTextParser("TOWN", TextAudioInfoType.FILE_OWNER);
		addTextParser("TPE1", TextAudioInfoType.LEAD_PERFORMER);
		addTextParser("TPE2", TextAudioInfoType.BAND);
		addTextParser("TPE3", TextAudioInfoType.CONDUCTOR);
		addTextParser("TPE4", TextAudioInfoType.MODIFIED_BY);
		addTextParser("TPOS", TextAudioInfoType.PART_OF_A_SET);
		addTextParser("TPRO", TextAudioInfoType.PRODUCED_NOTICE);
		addTextParser("TPUB", TextAudioInfoType.PUBLISHER);
		addTextParser("TRCK", TextAudioInfoType.TRACK_NUMBER);
		addTextParser("TRSN", TextAudioInfoType.RADIO_STATION);
		addTextParser("TRSO", TextAudioInfoType.RADIO_STATION_OWNER);
		addTextParser("TSOA", TextAudioInfoType.ALBUM_SORT_ORDER);
		addTextParser("TSOP", TextAudioInfoType.PERFORMER_SORT_ORDER);
		addTextParser("TSOT", TextAudioInfoType.TITLE_SORT_ORDER);
		addTextParser("TSRC", TextAudioInfoType.ISRC);
		addTextParser("TSSE", TextAudioInfoType.ENCODING_SETTINGS);
		addTextParser("TSST", TextAudioInfoType.SET_SUBTITLE);
		addTextParser("TXXX", TextAudioInfoType.USER_DEFINED_TEXT);
	
		addURLParser("WCOM", TextAudioInfoType.COMMERCIAL_INFO_URL);
		addURLParser("WCOP", TextAudioInfoType.COPYRIGHT_INFORMATION_URL);
		addURLParser("WOAF", TextAudioInfoType.AUDIO_FILE_WEBPAGE_URL);
		addURLParser("WOAR", TextAudioInfoType.PERFORMER_WEBPAGE_URL);
		addURLParser("WOAS", TextAudioInfoType.AUDIO_SOURCE_WEBPAGE_URL);
		addURLParser("WORS", TextAudioInfoType.RADIO_STATION_WEBPAGE_URL);
		addURLParser("WPAY", TextAudioInfoType.PAYMENT_URL);
		addURLParser("WPUB", TextAudioInfoType.PUBLISHERS_WEBPAGE_URL);
		
		// User defined URL is only supported for ISO_8859_1 encoding
		// as the encoding for the description will also be used for
		// the URL itself.
		addTextParser("WXXX", TextAudioInfoType.USER_DEFINED_URL);
	}
	
	static {
		loadParsers();
	}
}
