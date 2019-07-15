package com.g4mesoft.sound.format.aiff;

import java.io.IOException;

import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.IAudioFileProvider;

public class AiffFileProvider implements IAudioFileProvider {

	@Override
	public AudioFile loadAudioFile(AudioBitInputStream abis) throws IOException, AudioParsingException {
		return AiffFile.loadAIFF(abis);
	}

	@Override
	public Class<? extends AudioFile> getAudioFileClass() {
		return AiffFile.class;
	}
}
