package com.g4mesoft.sound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioFileProvider;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.mpeg.MPEGFileProvider;
import com.g4mesoft.sound.format.wav.WaveFile;
import com.g4mesoft.sound.format.wav.WaveFileProvider;
import com.g4mesoft.util.MemoryUtil;

public final class SoundManager {

	private static final int AUDIO_FILE_CAPACITY = 16;
	
	private static SoundManager instance;
	
	private final Mixer mixer;
	private final List<AudioFileProvider> providers;

	private AudioFile[] audioFiles;
	private int numAudioFiles;
	
	private SoundManager() {
		mixer = getOpenDefaultMixer();
		providers = new ArrayList<AudioFileProvider>();

		audioFiles = new AudioFile[AUDIO_FILE_CAPACITY];
		numAudioFiles = 0;
		
		initProviders();
	}

	private Mixer getOpenDefaultMixer() {
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		if (mixerInfos.length > 0)
			return AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]);

		throw new RuntimeException("No mixers available");
	}
	
	private void initProviders() {
		addAudioFileProvider(new WaveFileProvider());
		addAudioFileProvider(new MPEGFileProvider());
	}
	
	public void addAudioFileProvider(AudioFileProvider provider) {
		if (hasProviderClass(provider.getAudioFileClass()))
			throw new RuntimeException("Audio file is already supported!");
		providers.add(provider);
	}
	
	public int loadSound(InputStream is) throws IOException, AudioParsingException {
		AudioFile audioFile = loadAudioFile(is);
		if (audioFile == null)
			throw new AudioParsingException("Audio not supported!");
		
		ensureAudioFileCapacity();
		
		int id = -1;
		for (int i = 0; i < audioFiles.length; i++) {
			if (audioFiles[i] == null) {
				audioFiles[i] = audioFile;
				id = i;
				break;
			}
		}
		
		numAudioFiles++;
		
		return id;
	}
	
	private void ensureAudioFileCapacity() {
		if (numAudioFiles < audioFiles.length)
			return; // Enough room for at least one more
		
		synchronized(audioFiles) {
			// Double the audioFiles capacity
			audioFiles = Arrays.copyOf(audioFiles, audioFiles.length << 1);
		}
	}
	
	private AudioFile loadAudioFile(InputStream is) throws IOException, AudioParsingException {
		if (!is.markSupported())
			is = new BufferedInputStream(is);
		
		AudioFile audioFile = null;
		try {
			for (AudioFileProvider provider : providers)
				if ((audioFile = provider.loadAudioFile(is)) != null)
					break;
		} finally {
			is.close();
		}
		
		return audioFile;
	}
	
	public boolean unloadSound(int id) {
		if (id < 0 || id >= audioFiles.length)
			return false;
		
		AudioFile audioFile = audioFiles[id];
		if (audioFile != null) {
			audioFiles[id] = null;
			// audioFile.dispose()
			numAudioFiles--;
			return true;
		}
		
		return false;
	}
	
	public AudioFile getAudioFile(int id) {
		if (id < 0 || id >= audioFiles.length)
			return null;
		return audioFiles[id];
	}

	public boolean hasProviderClass(Class<? extends AudioFile> audioFileClass) {
		return getAudioFileClassProvider(audioFileClass) != null;
	}
	
	private AudioFileProvider getAudioFileClassProvider(Class<? extends AudioFile> audioFileClass) {
		for (AudioFileProvider provider : providers)
			if (provider.getAudioFileClass().isAssignableFrom(audioFileClass))
				return provider;
		
		return null;
	}
	
	public boolean playSound(int id, float volume) {
		return playSound(id, volume, true);
	}

	public boolean playSound(int id, float volume, boolean daemon) {
		AudioFile audioFile = getAudioFile(id);
		if (audioFile == null)
			return false;
		
		AudioThread audioThread;
		try {
			audioThread = new AudioThread(audioFile, volume);
		} catch(LineUnavailableException lue) {
			return false;
		}
		
		audioThread.setDaemon(daemon);
		audioThread.start();
		
		return true;
	}

	private SourceDataLine getSourceDataLine() throws LineUnavailableException {
		for (Line.Info lineInfo : mixer.getSourceLineInfo())
			if (SourceDataLine.class.isAssignableFrom(lineInfo.getLineClass()))
				return (SourceDataLine)mixer.getLine(lineInfo);
		
		throw new LineUnavailableException("Default mixer doesn't support outgoing audio streaming");
	}

	public int getNumAudioFiles() {
		return numAudioFiles;
	}
	
	public static SoundManager getInstance() {
		if (instance == null)
			instance = new SoundManager();
		return instance;
	}
	
	private class AudioThread extends Thread {
		
		private final AudioFile audioFile;
		private final SourceDataLine sdl;
		private final float volume;
		
		private AudioThread(AudioFile audioFile, float volume) throws LineUnavailableException {
			super("AudioThread");
			
			this.audioFile = audioFile;
			sdl = getSourceDataLine();
			this.volume = volume;
		}
		
		@Override
		public void run() {
			AudioFormat format = audioFile.getFormat();
			
			try {
				sdl.open(format);
			} catch (LineUnavailableException e) {
				return;
			}

			int bytesWritten = 0;
			int bytesToWrite = format.getFrameSize() << 6; // * 64
			byte[] block = new byte[bytesToWrite];
			
			sdl.start();
			
			while (true) {
				int br = audioFile.getData(block, bytesWritten, 0, bytesToWrite);
				if (br <= 0) 
					break;
				for (int i = br - 2; i >= 0; i -= 2) {
					// works for 16-bits only
					short sample = MemoryUtil.littleEndianToShort(block, i);
					MemoryUtil.writeLittleEndianShort(block, (short)(sample * volume), i);
				}
				
				bytesWritten += sdl.write(block, 0, br);
			}
			
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
			}
			
			sdl.stop();
			sdl.flush();
			sdl.close();
		}
	}
	
	public static void main(String[] args) throws Exception {
		int id = -1;
		try {
			id = SoundManager.getInstance().loadSound(WaveFile.class.getResourceAsStream("/assets/test.soundtest_2big.wav"));
		} catch(IOException | AudioParsingException e) {
		}
		
		if (id == -1) {
			System.out.println("Unsupported audio file!");
			return;
		}
		
		SoundManager.getInstance().playSound(id, 0.4f, false);
	}
}
