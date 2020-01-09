package com.g4mesoft.sound.format.info.id3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.TagParsingException;
import com.g4mesoft.sound.format.info.AudioInfo;
import com.g4mesoft.sound.format.info.AudioInfo.AudioInfoType;
import com.g4mesoft.sound.format.info.AudioTag;

public class ID3v2Tag extends AudioTag {
	
	private static final int ID3_MARKER_DEC = 0x494433;
	private static final int ID3_MARKER_BIT_SIZE = 3 * 8;
	
	public static final String ID3_NAME = "ID3v2";
	public static final byte MAX_VERSION = 0x04;
	
	public static final int UNSYNCHRONISATION = 0x80;
	public static final int EXTENDED_HEADER = 0x40;
	public static final int FOOTER = 0x10;

	public static final byte PADDING_BYTE = 0x00;
	
	private final AudioInfo[] information;
	
	private ID3v2Tag(AudioInfo[] information) {
		super(ID3_NAME);

		this.information = information;
	}
	
	@Override
	public AudioInfo[] getInformation() {
		return information;
	}

	@Override
	public boolean isSupportedInformation(AudioInfoType type) {
		return getFirstOccuringInformation(type) != null;
	}

	@Override
	public AudioInfo getFirstOccuringInformation(AudioInfoType type) {
		for (AudioInfo info : getInformation()) {
			if (info.getType() == type)
				return info;
		}
		return null;
	}

	public static ID3v2Tag loadTag(AudioBitInputStream abis) throws IOException, TagParsingException {
		if (!abis.findBitPattern(ID3_MARKER_DEC, ID3_MARKER_BIT_SIZE))
			return null;

		byte[] buffer = new byte[4];
		ID3Helper.readBytesSafe(abis, buffer, 2, 0);
		if (buffer[0] > MAX_VERSION) // Major version
			ID3Helper.unsupported();
		
		ID3Helper.readByteSafe(abis, buffer, 0);
		byte flags = buffer[0];
		if ((flags & 0xF) != 0) // Unsupported flags must be zero
			ID3Helper.corrupted();
		
		int size = ID3Helper.readSynchsafeInt(abis, buffer);
		
		int br = 0;
		
		if ((flags & UNSYNCHRONISATION) != 0)
			ID3Helper.unsupported();
		
		if ((flags & EXTENDED_HEADER) != 0) {
			int ehSize = ID3Helper.readSynchsafeInt(abis, buffer);
			br += 4;
			
			// Check if number of flag bytes is
			// as written in the definition (1).
			br += ID3Helper.readByteSafe(abis, buffer, 0);
			if (buffer[0] != 0x01)
				ID3Helper.corrupted();
			
			br += ID3Helper.readByteSafe(abis, buffer, 0);
			if ((buffer[0] & 0x8F) != 0) // Invalid flags
				ID3Helper.corrupted();

			// Skip extended header
			br += abis.skip(ehSize - 6);
		}
		
		// Make sure we're not at the end of
		// the stream.
		if (abis.isEndOfStream())
			ID3Helper.corrupted();
		
		// The ID3 tag can still be invalid
		// but at this point we can be pretty
		// sure that the tag is valid.
		abis.invalidateReadLimit();
		
		List<AudioInfo> information = new ArrayList<AudioInfo>();
		while (br < size) {
			try {
				br += FrameParserManager.readFrame(abis, buffer, information, true);
			} catch (TagParsingException tpe) {
				// We found an invalid frame.. try to
				// continue either way. Make sure we
				// don't read extra bytes from the stream.
				flags = 0;
				break;
			}
		}
		
		if (!isValidInformation(information))
			ID3Helper.corrupted();
		
		if ((flags & FOOTER) != 0) {
			// We already found the main header,
			// no need to get the data from the
			// footer, as it is simply a copy.
			
			// Skip footer (10 bytes always)
			abis.skip(10);
		}
		
		return new ID3v2Tag(information.toArray(new AudioInfo[information.size()]));
	}

	private static boolean isValidInformation(List<AudioInfo> information) {
		HashSet<AudioInfoType> types = new HashSet<AudioInfoType>(information.size());
		for (AudioInfo info : information) {
			AudioInfoType type = info.getType();
			if (info.allowMultiples())
				continue;
			
			if (types.contains(type))
				return false;
			types.add(type);
		}
		return true;
	}
}
