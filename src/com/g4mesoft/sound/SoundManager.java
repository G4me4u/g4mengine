package com.g4mesoft.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec3f;
import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.IAudioFileProvider;
import com.g4mesoft.sound.format.ReadLimitReachedException;
import com.g4mesoft.sound.format.SoundFormat;
import com.g4mesoft.sound.format.TagParsingException;
import com.g4mesoft.sound.format.aiff.AiffFileProvider;
import com.g4mesoft.sound.format.info.AudioTag;
import com.g4mesoft.sound.format.info.id3.ID3v2Tag;
import com.g4mesoft.sound.format.mpeg.MPEGFileProvider;
import com.g4mesoft.sound.format.wav.WaveFileProvider;
import com.g4mesoft.sound.processor.AudioSource;

public final class SoundManager {

	private static final int AUDIO_FILE_CAPACITY = 16;
	
	private static final int INPUT_BUFFER_SIZE = 1024 * 16;
	private static final int AUDIO_TAG_READ_LIMIT = 1024;
	private static final int AUDIO_READ_LIMIT = INPUT_BUFFER_SIZE;
	
	private static SoundManager instance;
	
	private final Mixer mixer;
	private final List<IAudioFileProvider> providers;
	
	private final List<SoundThread> soundThreads;

	private AudioFile[] audioFiles;
	private int numAudioFiles;
	
	private SoundManager() {
		mixer = getOpenDefaultMixer();
		providers = new ArrayList<IAudioFileProvider>();

		soundThreads = new LinkedList<SoundThread>();
		
		audioFiles = new AudioFile[AUDIO_FILE_CAPACITY];
		numAudioFiles = 0;
		
		initDefaultProviders();
	}

	private Mixer getOpenDefaultMixer() {
		Mixer mixer = AudioSystem.getMixer(null);
		
		if (mixer == null)
			throw new RuntimeException("Default mixer not available");
	
		return mixer;
	}
	
	private void initDefaultProviders() {
		addAudioFileProvider(new WaveFileProvider());
		addAudioFileProvider(new AiffFileProvider());
		addAudioFileProvider(new MPEGFileProvider());
	}
	
	public void addAudioFileProvider(IAudioFileProvider provider) {
		if (hasProviderClass(provider.getAudioFileClass()))
			throw new RuntimeException("Audio file is already supported!");
		providers.add(provider);
	}
	
	public int loadSound(InputStream is) throws IOException, AudioParsingException {
		AudioFile audioFile = loadAudioFile(is);
		if (audioFile == null)
			throw new AudioParsingException("Unsupported audio format!");
		
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
		AudioFile audioFile = null;
		try (AudioBitInputStream abis = new AudioBitInputStream(is, INPUT_BUFFER_SIZE)) {
			abis.prereadBytes(INPUT_BUFFER_SIZE);
			AudioTag tag = loadAudioTag(abis, true);

			long tagSize = abis.getBytesRead();
			if (tag != null && tagSize != 0)
				abis.prereadBytes(INPUT_BUFFER_SIZE);
			
			for (IAudioFileProvider provider : providers) {
				abis.setReadLimit(AUDIO_READ_LIMIT);
				
				try {
					audioFile = provider.loadAudioFile(abis);
				} catch (ReadLimitReachedException e) {
				}

				if (audioFile != null)
					break;
				
				long bytesToRestore = abis.getBytesRead() - tagSize;
				if (bytesToRestore > INPUT_BUFFER_SIZE)
					return null;
				
				abis.restoreBytes((int)bytesToRestore);
			}
			
			if (audioFile != null && audioFile.getAudioTag() == null) {
				if (tag == null)
					tag = loadAudioTag(abis, false);

				if (tag != null)
					audioFile.setAudioTag(tag);
			}
		}
		
		return audioFile;
	}
	
	private AudioTag loadAudioTag(AudioBitInputStream abis, boolean resetOnError) throws IOException, AudioParsingException {
		AudioTag tag = null;

		long bytesRead = abis.getBytesRead();
		abis.setReadLimit(AUDIO_TAG_READ_LIMIT);
		
		try {
			tag = ID3v2Tag.loadTag(abis);
		} catch (TagParsingException | ReadLimitReachedException e) {
		}
		
		if (tag == null && resetOnError) {
			long bytesToRestore = abis.getBytesRead() - bytesRead;
			if (bytesToRestore > INPUT_BUFFER_SIZE)
				throw new AudioParsingException("Invalid audio tag");
			
			abis.restoreBytes((int)bytesToRestore);
		}
		
		return tag;
	}
	
	public boolean unloadSound(int id) {
		if (id < 0 || id >= audioFiles.length)
			return false;
		
		AudioFile audioFile = audioFiles[id];
		if (audioFile != null) {
			audioFiles[id] = null;
			audioFile.dispose();
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
	
	private IAudioFileProvider getAudioFileClassProvider(Class<? extends AudioFile> audioFileClass) {
		for (IAudioFileProvider provider : providers)
			if (provider.getAudioFileClass().isAssignableFrom(audioFileClass))
				return provider;
		
		return null;
	}
	
	public AudioSource playSound(int id) throws LineUnavailableException {
		return playSound(id, true);
	}

	public AudioSource playSound(int id, boolean daemon) throws LineUnavailableException {
		AudioFile audioFile = getAudioFile(id);
		if (audioFile == null)
			return null;
		
		AudioSource audioSource = new AudioSource(audioFile, new Vec3f());

		while (true) {
			SoundThread soundThread = getAppropriateSoundThread(audioSource.getFormat());
			if (soundThread.addAudioSource(audioSource))
				break;
			
			try {
				soundThread.join();
			} catch (InterruptedException e) {
			}

			soundThreads.remove(soundThread);
		};

		return audioSource;
	}
	
	private SoundThread getAppropriateSoundThread(SoundFormat format) throws LineUnavailableException {
		float sampleRate = format.getSampleRate();
		
		for (SoundThread soundThread : soundThreads) {
			if (MathUtils.nearZero(soundThread.getSampleRate() - sampleRate))
				return soundThread;
		}
		
		SoundThread thread = new SoundThread(getSourceDataLine(), sampleRate);
		soundThreads.add(thread);
		
		return thread;
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
}
