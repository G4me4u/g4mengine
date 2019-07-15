package com.g4mesoft.sound;

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

import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec3f;
import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.ReadLimitReachedException;
import com.g4mesoft.sound.format.IAudioFileProvider;
import com.g4mesoft.sound.format.TagParsingException;
import com.g4mesoft.sound.format.aiff.AiffFileProvider;
import com.g4mesoft.sound.format.info.AudioTag;
import com.g4mesoft.sound.format.info.id3.ID3v2Tag;
import com.g4mesoft.sound.format.mpeg.MPEGFileProvider;
import com.g4mesoft.sound.format.wav.WaveFileProvider;
import com.g4mesoft.sound.processor.AudioChannel;
import com.g4mesoft.sound.processor.AudioSource;
import com.g4mesoft.util.MemoryUtil;

public final class SoundManager {

	private static final int AUDIO_FILE_CAPACITY = 16;
	
	private static final float SAMPLE_MULTIPLIER = 32768;
	private static final float SAMPLE_DIVIDER = 1.0f / SAMPLE_MULTIPLIER;
	public static final int SAMPLES_PER_PLAYBACK = 4096;
	
	private static final int INPUT_BUFFER_SIZE = 1024 * 16;
	private static final int AUDIO_TAG_READ_LIMIT = 1024;
	private static final int AUDIO_READ_LIMIT = INPUT_BUFFER_SIZE;
	
	private static SoundManager instance;
	
	private final Mixer mixer;
	private final List<IAudioFileProvider> providers;

	private AudioFile[] audioFiles;
	private int numAudioFiles;
	
	private SoundManager() {
		mixer = getOpenDefaultMixer();
		providers = new ArrayList<IAudioFileProvider>();

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
		
		AudioSource source = new AudioSource(audioFile, new Vec3f());
		
		AudioThread audioThread = new AudioThread(source);
		
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
		
		private final int numChannelsIn;
		
		private AudioThread(AudioSource audioSource) throws LineUnavailableException {
			super("AudioThread");
			
			this.audioSource = audioSource;

			sdl = getSourceDataLine();
		
			numChannelsIn = audioSource.getFormat().getChannels();
		}
		
		private void processChannel(byte[] blockIn, byte[] blockOut, float[] samples, int framesIn, AudioChannel channel) {
			int fp, i;

			if (numChannelsIn == 1) {
				for (fp = 0, i = 0; fp < framesIn; fp++, i += 2)
					samples[fp] = MemoryUtil.littleEndianToShort(blockIn, i) * SAMPLE_DIVIDER;
			} else {
				for (fp = 0, i = (channel == AudioChannel.LEFT) ? 0 : 2; fp < framesIn; fp++, i += 4)
					samples[fp] = MemoryUtil.littleEndianToShort(blockIn, i) * SAMPLE_DIVIDER;
			}

			audioSource.preProcess(samples, framesIn, channel);

			if (blockOut != null) {
				for (fp = 0, i = (channel == AudioChannel.LEFT) ? 0 : 2; fp < framesIn; fp++, i += 4)
					MemoryUtil.writeLittleEndianShort(blockOut, (short)(samples[fp] * SAMPLE_MULTIPLIER), i);
			}
		}
		
		private int readAndProcessFrames(int framesToRead, byte[] blockIn, byte[] blockOut, float[] samples) {
			int fr = audioSource.readRawFrames(blockIn, framesToRead);
			if (fr <= 0) 
				return -1;
			
			processChannel(blockIn, blockOut, samples, fr, AudioChannel.LEFT);
			processChannel(blockIn, blockOut, samples, fr, AudioChannel.RIGHT);
		
			return fr;
		}
		
		@Override
		public void run() {
			AudioFormat formatIn = audioSource.audioFile.getFormat();
			
			AudioFormat formatOut;
			if (numChannelsIn == 1) { // Mono
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
			
			int frameSize = formatOut.getFrameSize();
			int bytesToRead = framesToRead * frameSize;

			int prevLatency = 0;
			while (audioSource.isPlaying()) {
				if (sdl.getBufferSize() - sdl.available() < bytesToRead) {
					int latency = audioSource.getSampleProcessingLatency();
					if (prevLatency < latency) {
						// Convert from samples to frames. The frame size is
						// defined in bytes (including both channels), so we
						// have to also multiply the latency by the number of
						// channels.
						int byteLatency = 2 * (latency - prevLatency);
						int frameLatency = (numChannelsIn * byteLatency) / frameSize;
						prevLatency = latency;
						
						do {
							int framesToSkip = MathUtils.min(frameLatency, framesToRead);
							
							// Make sure not to write to blockOut.
							int fr = readAndProcessFrames(framesToSkip, blockIn, null, samples);
							if (fr == -1)
								break;

							frameLatency -= fr;
						} while (frameLatency != 0);
					}
					
					int fr = readAndProcessFrames(framesToRead, blockIn, blockOut, samples);
					if (fr == -1)
						break;
					
					sdl.write(blockOut, 0, fr * frameSize);
				} else {
					try {
						Thread.sleep(1L);
					} catch (InterruptedException e) {
					}
				}
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
}
