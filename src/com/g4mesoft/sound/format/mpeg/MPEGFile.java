package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.BasicAudioFile;
import com.g4mesoft.sound.format.SoundFormat;

public class MPEGFile extends BasicAudioFile {

	/**
	 * The minimum number of frames in the MPEG file for it to be considered as
	 * a valid MPEG file. If this number of frames is not reached before the
	 * read limit set by the {@code SoundManager}, then the audio file is not
	 * considered as an MPEG file.
	 */
	private static final int MIN_FRAMES = 3;
	
	private MPEGFile(byte[] data, SoundFormat format) {
		super(data, format);
	}
	
	public static MPEGFile loadMPEG(AudioBitInputStream abis) throws IOException, AudioParsingException {
		MPEGFrameDecoder frameDecoder = new MPEGFrameDecoder();
		
		while (frameDecoder.getNumValidFrames() < MIN_FRAMES) {
			if (!frameDecoder.readNextFrame(abis))
				return null;
		}
		
		abis.invalidateReadLimit();
		
		while (frameDecoder.readNextFrame(abis)) { }
		
		frameDecoder.flushCachedSamples();
		
		byte[] samples = frameDecoder.getCompiledSamples();
		SoundFormat format = frameDecoder.getFormat();
		return new MPEGFile(samples, format);
	}
}
