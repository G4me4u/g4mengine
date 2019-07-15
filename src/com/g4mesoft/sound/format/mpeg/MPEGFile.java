package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.BasicAudioFile;

public class MPEGFile extends BasicAudioFile {

	/**
	 * The minimum number of frames in the MPEG file for it to be considered as
	 * a valid MPEG file. If this number of frames is not reached before the
	 * read limit set by the {@code SoundManager}, then the audio file is not
	 * considered as an MPEG file.
	 */
	private static final int MIN_FRAMES = 3;
	
	private MPEGFile(byte[] data, AudioFormat format) {
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
		
		AudioFormat format = new AudioFormat(Encoding.PCM_SIGNED, 
		                                     frameDecoder.getSampleRate(),
		                                     16,
		                                     2, 
		                                     4, 
		                                     frameDecoder.getSampleRate(),
		                                     false);
		
		return new MPEGFile(frameDecoder.getCompiledSamples(), format);
	}

	public static AudioFormat getAudioFormat(AudioFormat.Encoding encoding, int sampleRate, int sampleSizeInBits, int channels, int frameSize, boolean bigEndian) {
		return new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, sampleRate * (sampleSizeInBits >>> 3) * channels / frameSize, bigEndian);
	}
}
