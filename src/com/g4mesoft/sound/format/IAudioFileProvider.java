package com.g4mesoft.sound.format;

import java.io.IOException;

public interface IAudioFileProvider {

	public AudioFile loadAudioFile(AudioBitInputStream is) throws IOException, AudioParsingException;

	public Class<? extends AudioFile> getAudioFileClass();
	
}
