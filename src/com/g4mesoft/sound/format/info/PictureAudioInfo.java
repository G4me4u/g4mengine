package com.g4mesoft.sound.format.info;

import java.awt.Image;

public class PictureAudioInfo extends AudioInfo {

	private final PictureType picType;
	private final String desc;
	private final String url;
	private final Image pic;
	
	public PictureAudioInfo(PictureType picType, String desc, Image pic, AudioInfoType type) {
		super(type);
		
		this.picType = picType;
		this.desc = desc;
		this.url = null;
		this.pic = pic;
	}

	public PictureAudioInfo(PictureType picType, String desc, String url, AudioInfoType type) {
		super(type);
	
		this.picType = picType;
		this.desc = desc;
		this.url = url;
		this.pic = null;
	}

	public PictureType getPictureType() {
		return picType;
	}
	
	public boolean isURL() {
		return url != null;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public String getURL() {
		return url;
	}
	
	public Image getPicture() {
		return pic;
	}
	
	@Override
	public boolean allowMultiples() {
		return true;
	}
	
	@Override
	public String toString() {
		if (isURL())
			return String.format("%s(%s), %s: %s", type, picType, desc, url);
		return String.format("%s(%s), %s: [image]", type, picType, desc);
	}
	
	public static final class PictureAudioInfoType extends AudioInfoType {

		public static final PictureAudioInfoType ATTACHED_PICTURE = new PictureAudioInfoType("Attached picture");
		
		private PictureAudioInfoType(String name) {
			super(name);
		}
	}
	
	public static enum PictureType {
		
		OTHER(0x00, "Other"),
		PNG32_FILE_ICON(0x01, "32x32 pixels file icon (PNG only)"),
		OTHER_FILE_ICON(0x02, "Other file icon"),
		FRONT_COVER(0x03, "Cover (front)"),
		BACK_COVER(0x04, "Cover (back)"),
		LEAFLET_PAGE(0x05, "Leaflet page"),
		MEDIA(0x06, "Media"),
		LEAD_PERFORMER(0x07, "Lead artist/performer/soloist"),
		PERFORMER(0x08, "Artist/Performer"),
		CONDUCTOR(0x09, "Conductor"),
		BAND(0x0A, "Band/Orchestra"),
		COMPOSER(0x0B, "Composer"),
		LYRICIST(0x0C, "Lyricist/Text writer"),
		RECORDING_LOCATION(0x0D, "Recording location"),
		DURING_RECORDING(0x0E, "During recording"),
		DURING_PERFORMANCE(0x0F, "During performance"),
		SCREEN_CAPTURE(0x10, "Movie/Video screen capture"),
		A_BRIGHT_COLOURED_FISH(0x11, "A bright coloured fish?"),
		ILLUSTRATION(0x12, "Illustration"),
		BAND_LOGOTYPE(0x13, "Band/Artist logotype"),
		PUBLISHER_LOGOTYPE(0x14, "Publisher/Studio logotype");

		private static final PictureType[] ID_TO_TYPE;
		
		private final int id;
		private final String name;
		
		private PictureType(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public int getID() {
			return id;
		}
		
		public static PictureType fromID(int id) {
			if (id < 0 || id >= ID_TO_TYPE.length)
				return null;
			return ID_TO_TYPE[id];
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		static {
			ID_TO_TYPE = new PictureType[values().length];
			for (PictureType type : values())
				ID_TO_TYPE[type.id] = type;
		}
	}
}
