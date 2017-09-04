package com.g4mesoft.sound.format;

import java.io.IOException;
import java.io.InputStream;

public interface AudioFileProvider {

	public AudioFile loadAudioFile(InputStream is) throws IOException, AudioParsingException;
	
}
