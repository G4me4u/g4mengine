package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import com.g4mesoft.sound.SoundManager;
import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.TagParsingException;
import com.g4mesoft.sound.format.info.TextAudioInfo;
import com.g4mesoft.sound.format.info.TextAudioInfo.TextAudioInfoType;
import com.g4mesoft.sound.format.info.id3.ID3v2Tag;
import com.g4mesoft.util.MemoryUtil;

public class MP3File extends AudioFile {

	/**
	 * The maximum number of bytes able to get reset
	 * by the InputStream.
	 */
	private static final int MAX_TOLERANCE_DEPTH = 1024;

	/**
	 * The file type specified in ID3 tags for MPEG.
	 */
	private static final String MPEG_FILE_TYPE = "MPG";
	
	private final byte[] data;
	private final AudioFormat format;
	private final ID3v2Tag audioTag;
	
	MP3File(byte[] data, AudioFormat format, ID3v2Tag audioTag) {
		this.data = data;
		this.format = format;
		this.audioTag = audioTag;
	}
	
	public static MP3File loadMP3(InputStream is) throws IOException, AudioParsingException {
		if (!is.markSupported())
			return null;
		is.mark(Integer.MAX_VALUE);
		
		ID3v2Tag tag = null;
		try {
			tag = ID3v2Tag.loadTag(is);
		} catch(TagParsingException e) {
		}

		if (tag != null) {
			TextAudioInfo type = (TextAudioInfo)tag.getFirstOccuringInformation(TextAudioInfoType.FILE_TYPE);
			if (type != null && !type.getValue()[0].startsWith(MPEG_FILE_TYPE)) {
				is.reset();
				return null;
			}
		} else {
			is.reset(); // We need to reset
			is.mark(MAX_TOLERANCE_DEPTH);
		}
		
		MP3BitStream bitStream = new MP3BitStream(is);
		MPEGFrame frame = new MPEGFrame();
		
		int numSamples = 0;
		List<float[]> data = new ArrayList<float[]>();
		
		int numFrames = 0;
		
		long oldByteLocation = 0;
		
		while (true) {
			if (!frame.readFrame(bitStream))
				break; // End of stream
			
			System.out.println("Last frame was approx. " + (frame.header.byteLocation - oldByteLocation) + " bytes long");
			System.out.println("\nFrame at position: " + Long.toString(bitStream.getBytesRead()));
			oldByteLocation = frame.header.byteLocation;
			
			switch(frame.header.version) {
			case MPEGHeader.MPEG_V10:
				System.out.println("Frame is mpeg 1");
				break;
			case MPEGHeader.MPEG_V20:
				System.out.println("Frame is mpeg 2");
				break;
			case MPEGHeader.MPEG_V25:
				System.out.println("Frame is mpeg 2.5");
				break;
			}
			
			switch(frame.header.layer) {
			case MPEGHeader.LAYER_I:
				System.out.println("Frame is layer 1");
				break;
			case MPEGHeader.LAYER_II:
				System.out.println("Frame is layer 2");
				break;
			case MPEGHeader.LAYER_III:
				System.out.println("Frame is layer 3");
				break;
			}
			
			switch(frame.header.mode) {
			case MPEGHeader.SINGLE_CHANNEL:
				System.out.println("Mono");
				break;
			case MPEGHeader.STEREO:
				System.out.println("Stereo");
				break;
			case MPEGHeader.DUAL_CHANNEL:
				System.out.println("Dual channel");
				break;
			case MPEGHeader.JOINT_STEREO:
				System.out.println("Joint sterio");
				break;
			}
			
			System.out.println(frame.bitrate + " bits per second");
			System.out.println(frame.frequency + " Hz");
			
			if (frame.header.layer != MPEGHeader.LAYER_III) {
				float[] samples = frame.header.layer == MPEGHeader.LAYER_I ? frame.audioLayer1.getSamples() : frame.audioLayer2.getSamples();
				numSamples += samples.length;
				data.add(samples);
			}
			
			if (numFrames++ > 50000) break;
		}
		
		System.out.println("\nNumber of valid frames: " + Integer.toString(numFrames));
		
		float mx = 0.0f, mn = 0.0f;
		
		int clips = 0;
		
		int br = 0;
		byte[] dat = new byte[numSamples * 2];
		for (float[] samples : data) {
			for (float sample : samples) {
				if (sample > mx)
					mx = sample;
				if (sample < mn)
					mn = sample;
				sample *= Short.MAX_VALUE;
				if (sample > Short.MAX_VALUE) {
					sample = Short.MAX_VALUE;
					clips++;
				} else if (sample < Short.MIN_VALUE) {
					sample = Short.MIN_VALUE;
					clips++;
				}
				MemoryUtil.writeLittleEndianShort(dat, (short)sample, br);
				br += 2;
			}
		}
		System.out.println(mx + ", " + mn);
		System.out.println(clips + " clips");
		System.out.println((float)clips / numSamples * 100.0f + "%");
		data.clear();
		data = null;
		
		is.mark(-1);
		
		return new MP3File(dat, getAudioFormat(AudioFormat.Encoding.PCM_SIGNED, frame.frequency, 16, 2, 4, false), tag);
	}

	public static AudioFormat getAudioFormat(AudioFormat.Encoding encoding, int sampleRate, int sampleSizeInBits, int channels, int frameSize, boolean bigEndian) {
		return new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, sampleRate * (sampleSizeInBits >>> 3) * channels / frameSize, bigEndian);
	}

	@Override
	public AudioFormat getFormat() {
		return format;
	}

	@Override
	public int getData(byte[] dst, int srcPos, int dstPos, int len) {
		if (len > data.length - srcPos)
			len = data.length - srcPos;
		System.arraycopy(data, srcPos, dst, dstPos, len);
		return len;
	}
	
	@Override
	public long getLengthInFrames() {
		return data.length / format.getFrameSize();
	}

	public ID3v2Tag getAudioTag() {
		return audioTag;
	}
	
	public static void main(String[] args) throws IOException, AudioParsingException {
		int id = SoundManager.getInstance().loadSound(MP3File.class.getResourceAsStream("/assets/test.audio.mp1"));
		if (id == -1)
			return;
		
		AudioFile file = SoundManager.getInstance().getAudioFile(id);
		if (file != null && file instanceof MP3File) {
			ID3v2Tag tag = ((MP3File)file).getAudioTag();
			System.out.println(tag);
		}
		
		SoundManager.getInstance().playSound(id, 0.4f, false);
	}
}
