package com.g4mesoft.sound.format.wav;

import java.io.IOException;
import java.io.InputStream;

import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioFileProvider;
import com.g4mesoft.sound.format.AudioParsingException;

public class WaveFileProvider implements AudioFileProvider {

	@Override
	public AudioFile loadAudioFile(InputStream is) throws IOException, AudioParsingException {
		return WaveFile.loadWave(is);
	}
}
