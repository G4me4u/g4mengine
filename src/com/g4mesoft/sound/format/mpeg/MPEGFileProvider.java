package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;
import java.io.InputStream;

import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioFileProvider;
import com.g4mesoft.sound.format.AudioParsingException;

public class MPEGFileProvider implements AudioFileProvider {

	@Override
	public AudioFile loadAudioFile(InputStream is) throws IOException, AudioParsingException {
		return MPEGFile.loadMPEG(is);
	}

	@Override
	public Class<? extends AudioFile> getAudioFileClass() {
		return MPEGFile.class;
	}
}
