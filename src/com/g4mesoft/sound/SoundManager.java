package com.g4mesoft.sound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import com.g4mesoft.math.Vec3f;
import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioFileProvider;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.mpeg.MPEGFileProvider;
import com.g4mesoft.sound.format.wav.WaveFileProvider;
import com.g4mesoft.sound.processor.AudioChannel;
import com.g4mesoft.sound.processor.AudioSource;
import com.g4mesoft.util.MemoryUtil;

public final class SoundManager {

	private static final int AUDIO_FILE_CAPACITY = 16;
	
	private static final float SAMPLE_MULTIPLIER = 32768;
	private static final float SAMPLE_DIVIDER = 1.0f / SAMPLE_MULTIPLIER;
	private static final int SAMPLES_PER_PLAYBACK = 512;
	
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
	
	public boolean hasAudioFile(int id) {
		return getAudioFile(id) != null;
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
	
	public AudioSource playSound(int id) {
		return playSound(id, true);
	}

	public AudioSource playSound(int id, boolean daemon) {
		AudioFile audioFile = getAudioFile(id);
		if (audioFile == null)
			return null;
		
		AudioSource source = new AudioSource(audioFile, new Vec3f());
		
		AudioThread audioThread;
		try {
			audioThread = new AudioThread(source);
		} catch(LineUnavailableException lue) {
			return null;
		}
		
		audioThread.setDaemon(daemon);
		audioThread.start();
		
		return source;
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
		
		private final AudioSource audioSource;
		private final SourceDataLine sdl;
		
		private AudioThread(AudioSource audioSource) throws LineUnavailableException {
			super("AudioThread");
			
			this.audioSource = audioSource;

			sdl = getSourceDataLine();
		}
		
		private void processChannel(byte[] blockIn, byte[] blockOut, float[] samples, int framesIn, boolean monoIn, AudioChannel channel) {
			int fp, i;

			if (monoIn) {
				for (fp = 0, i = 0; fp < framesIn; fp++, i += 2)
					samples[fp] = MemoryUtil.littleEndianToShort(blockIn, i) * SAMPLE_DIVIDER;
			} else {
				for (fp = 0, i = (channel == AudioChannel.LEFT) ? 0 : 2; fp < framesIn; fp++, i += 4)
					samples[fp] = MemoryUtil.littleEndianToShort(blockIn, i) * SAMPLE_DIVIDER;
			}

			audioSource.preProcess(samples, framesIn, channel);

			for (fp = 0, i = (channel == AudioChannel.LEFT) ? 0 : 2; fp < framesIn; fp++, i += 4)
				MemoryUtil.writeLittleEndianShort(blockOut, (short)(samples[fp] * SAMPLE_MULTIPLIER), i);
		}
		
		@Override
		public void run() {
			AudioFormat formatIn = audioSource.audioFile.getFormat();
			
			boolean monoIn = formatIn.getChannels() == 1;
			
			AudioFormat formatOut;
			if (monoIn) { // Mono
				formatOut = new AudioFormat(formatIn.getEncoding(), 
				                            formatIn.getSampleRate(), 
				                            formatIn.getSampleSizeInBits(), 
				                            2, 
				                            formatIn.getFrameSize() << 1, 
				                            formatIn.getFrameRate(), 
				                            false);
			} else {
				formatOut = formatIn;
			}
			
			try {
				sdl.open(formatOut);
			} catch (LineUnavailableException e) {
				return;
			}

			int framesToRead = SAMPLES_PER_PLAYBACK;
			byte[] blockIn = new byte[framesToRead * formatIn.getFrameSize()];
			byte[] blockOut = new byte[framesToRead * formatOut.getFrameSize()];
			
			// A single frame will contain samples for both channels.
			// NOTE: We reuse 'samples' for both channels.
			float[] samples = new float[framesToRead];
			
			sdl.start();
			
			while (audioSource.isPlaying()) {
				int fr = audioSource.readFrames(blockIn, framesToRead);
				if (fr <= 0) 
					break;
				
				processChannel(blockIn, blockOut, samples, fr, monoIn, AudioChannel.LEFT);
				processChannel(blockIn, blockOut, samples, fr, monoIn, AudioChannel.RIGHT);
				
				sdl.write(blockOut, 0, fr * formatOut.getFrameSize());
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
			//InputStream is = WaveFile.class.getResourceAsStream("/assets/test.soundtest_2big.wav");
			URL url = new URL("http://www.class-connection.com/dealers/8bit-ulaw/Female-Voice/Saturday%20Female%20Voice.wav");
			id = SoundManager.getInstance().loadSound(url.openStream());
		} catch(IOException | AudioParsingException e) {
		}
		
		if (id == -1) {
			System.out.println("Unsupported audio file!");
			return;
		}
		
		SoundManager.getInstance().playSound(id, false).setVolume(0.05f);
	}
}
