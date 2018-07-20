package com.g4mesoft.sound.format.wav;

import java.io.IOException;
import java.io.InputStream;

import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.IAudioFileProvider;
import com.g4mesoft.sound.format.AudioParsingException;

public class WaveFileProvider implements IAudioFileProvider {

	@Override
	public AudioFile loadAudioFile(InputStream is) throws IOException, AudioParsingException {
		return WaveFile.loadWave(is);
	}

	@Override
	public Class<? extends AudioFile> getAudioFileClass() {
		return WaveFile.class;
	}
}
