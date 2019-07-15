package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.IAudioFileProvider;

public class MPEGFileProvider implements IAudioFileProvider {

	@Override
	public AudioFile loadAudioFile(AudioBitInputStream abis) throws IOException, AudioParsingException {
		return MPEGFile.loadMPEG(abis);
	}

	@Override
	public Class<? extends AudioFile> getAudioFileClass() {
		return MPEGFile.class;
	}
}
