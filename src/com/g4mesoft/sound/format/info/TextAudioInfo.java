package com.g4mesoft.sound.format.info;

import java.util.Arrays;

public class TextAudioInfo extends AudioInfo {

	protected final String[] value;
	
	public TextAudioInfo(String[] value, TextAudioInfoType type) {
		super(type);
		
		this.value = value;
	}
	
	public String[] getValue() {
		return value;
	}
	
	@Override
	public boolean allowMultiples() {
		return type == TextAudioInfoType.USER_DEFINED_TEXT || 
			   type == TextAudioInfoType.USER_DEFINED_URL;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", type, Arrays.toString(value));
	}

	public static final class TextAudioInfoType extends AudioInfoType {

		public static final TextAudioInfoType ALBUM_TITLE 				= new TextAudioInfoType("Album title");
		public static final TextAudioInfoType BEATS_PER_MINUTE 			= new TextAudioInfoType("Beats per minute");
		public static final TextAudioInfoType COMPOSER 					= new TextAudioInfoType("Composer");
		public static final TextAudioInfoType CONTENT_TYPE 				= new TextAudioInfoType("Content type");
		public static final TextAudioInfoType COPYRIGHT_MESSSAGE 		= new TextAudioInfoType("Copyright message");
		public static final TextAudioInfoType ENCODING_TIME 			= new TextAudioInfoType("Encoding time");
		public static final TextAudioInfoType PLAYLIST_DELAY 			= new TextAudioInfoType("Playlist delay");
		public static final TextAudioInfoType ORIGINAL_RELEASE_TIME 	= new TextAudioInfoType("Original release time");
		public static final TextAudioInfoType RECORDING_TIME 			= new TextAudioInfoType("Recording time");
		public static final TextAudioInfoType RELEASE_TIME 				= new TextAudioInfoType("Release time");
		public static final TextAudioInfoType TAGGING_TIME 				= new TextAudioInfoType("Tagging time");
		public static final TextAudioInfoType ENCODED_BY 				= new TextAudioInfoType("Encoded by");
		public static final TextAudioInfoType LYRICIST 					= new TextAudioInfoType("Lyricist/Text writer");
		public static final TextAudioInfoType FILE_TYPE 				= new TextAudioInfoType("File type");
		public static final TextAudioInfoType INVOLVED_PEOPLE 			= new TextAudioInfoType("Involved people list");
		public static final TextAudioInfoType CONTENT_GROUP_DESCRIPTION = new TextAudioInfoType("Content group description");
		public static final TextAudioInfoType TITLE 					= new TextAudioInfoType("Title/Songname");
		public static final TextAudioInfoType SUBTITLE 					= new TextAudioInfoType("Subtitle");
		public static final TextAudioInfoType INITIAL_KEY 				= new TextAudioInfoType("Initial key");
		public static final TextAudioInfoType LANGUAGE 					= new TextAudioInfoType("Language(s)");
		public static final TextAudioInfoType LENGTH 					= new TextAudioInfoType("Length");
		public static final TextAudioInfoType MUSICIAN_CREDITS_LIST 	= new TextAudioInfoType("Musician credits list");
		public static final TextAudioInfoType MEDIA_TYPE 				= new TextAudioInfoType("Media type");
		public static final TextAudioInfoType MOOD 						= new TextAudioInfoType("Mood");
		public static final TextAudioInfoType ORIGINAL_ALBUM_TITLE 		= new TextAudioInfoType("Original album title");
		public static final TextAudioInfoType ORIGINAL_FILENAME 		= new TextAudioInfoType("Original filename");
		public static final TextAudioInfoType ORIGINAL_LYRICIST 		= new TextAudioInfoType("Original lyricist(s)/text writer(s)");
		public static final TextAudioInfoType ORIGINAL_ARTIST 			= new TextAudioInfoType("Original artist(s)/performer(s)");
		public static final TextAudioInfoType FILE_OWNER 				= new TextAudioInfoType("File owner/licensee");
		public static final TextAudioInfoType LEAD_PERFORMER 			= new TextAudioInfoType("Lead performer(s)/Soloist(s)");
		public static final TextAudioInfoType BAND 						= new TextAudioInfoType("Band/orchestra/accompaniment");
		public static final TextAudioInfoType CONDUCTOR 				= new TextAudioInfoType("Conductor");
		public static final TextAudioInfoType MODIFIED_BY 				= new TextAudioInfoType("Interpreted, remixed, or otherwise modified by");
		public static final TextAudioInfoType PART_OF_A_SET 			= new TextAudioInfoType("Part of a set");
		public static final TextAudioInfoType PRODUCED_NOTICE 			= new TextAudioInfoType("Produced notice");
		public static final TextAudioInfoType PUBLISHER 				= new TextAudioInfoType("Publisher");
		public static final TextAudioInfoType TRACK_NUMBER 				= new TextAudioInfoType("Track number/Position in set");
		public static final TextAudioInfoType RADIO_STATION 			= new TextAudioInfoType("Internet radio station name");
		public static final TextAudioInfoType RADIO_STATION_OWNER 		= new TextAudioInfoType("Internet radio station owner");
		public static final TextAudioInfoType ALBUM_SORT_ORDER 			= new TextAudioInfoType("Album sort order");
		public static final TextAudioInfoType PERFORMER_SORT_ORDER 		= new TextAudioInfoType("Performer sort order");
		public static final TextAudioInfoType TITLE_SORT_ORDER 			= new TextAudioInfoType("Title sort order");
		public static final TextAudioInfoType ISRC 						= new TextAudioInfoType("ISRC (international standard recording code)");
		public static final TextAudioInfoType ENCODING_SETTINGS 		= new TextAudioInfoType("Software/Hardware and settings used for encoding");
		public static final TextAudioInfoType SET_SUBTITLE 				= new TextAudioInfoType("Set subtitle");
		public static final TextAudioInfoType USER_DEFINED_TEXT 		= new TextAudioInfoType("User defined text information");
		
		public static final TextAudioInfoType COMMERCIAL_INFO_URL 		= new TextAudioInfoType("Commercial information");
		public static final TextAudioInfoType COPYRIGHT_INFORMATION_URL = new TextAudioInfoType("Copyright/Legal information");
		public static final TextAudioInfoType AUDIO_FILE_WEBPAGE_URL 	= new TextAudioInfoType("Official audio file webpage");
		public static final TextAudioInfoType PERFORMER_WEBPAGE_URL 	= new TextAudioInfoType("Official artist/performer webpage");
		public static final TextAudioInfoType AUDIO_SOURCE_WEBPAGE_URL 	= new TextAudioInfoType("Official audio source webpage");
		public static final TextAudioInfoType RADIO_STATION_WEBPAGE_URL = new TextAudioInfoType("Official Internet radio station homepage");
		public static final TextAudioInfoType PAYMENT_URL 				= new TextAudioInfoType("Payment");
		public static final TextAudioInfoType PUBLISHERS_WEBPAGE_URL 	= new TextAudioInfoType("Publishers official webpage");
		public static final TextAudioInfoType USER_DEFINED_URL 			= new TextAudioInfoType("User defined URL link");
		
		private TextAudioInfoType(String name) {
			super(name);
		}
	}
}
