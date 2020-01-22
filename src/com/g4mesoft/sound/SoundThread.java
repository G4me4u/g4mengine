package com.g4mesoft.sound;

import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.g4mesoft.math.MathUtils;
import com.g4mesoft.sound.convert.Audio16BitSampleConverter;
import com.g4mesoft.sound.convert.Audio24BitSampleConverter;
import com.g4mesoft.sound.convert.Audio32BitSampleConverter;
import com.g4mesoft.sound.convert.Audio8BitSampleConverter;
import com.g4mesoft.sound.convert.ISampleConverter;
import com.g4mesoft.sound.format.SoundEncoding;
import com.g4mesoft.sound.format.SoundFormat;
import com.g4mesoft.sound.processor.AudioChannel;
import com.g4mesoft.sound.processor.AudioSource;

public class SoundThread extends Thread {
	
	private static final int BUFFER_MS_PER_PLAYBACK = 50;
	private static final float MS_PER_SECOND = 1000.0f;
	
	private static final int NUM_BYTES_FOR_8BIT  = 1;
	private static final int NUM_BYTES_FOR_16BIT = 2;
	private static final int NUM_BYTES_FOR_24BIT = 3;
	private static final int NUM_BYTES_FOR_32BIT = 4;
	
	private final SourceDataLine sdl;
	private final float sampleRate;
	private final int framesPerPlayback;
	
	private final ISampleConverter outSampleConverter;
	private final AudioFormat outFormat;
	
	private volatile AudioNode rootNode;
	private final Object nodeLock;
	
	private volatile boolean running;
	private volatile boolean starting;
	
	private volatile boolean runPermanantly;
	
	private boolean hasStarted;
	private final Object bootLock;
	
	SoundThread(SourceDataLine sdl, float sampleRate) throws LineUnavailableException {
		super(String.format("AudioThread - %.2fHz", sampleRate));
		
		this.sdl = sdl;
		this.sampleRate = sampleRate;
		framesPerPlayback = (int)MathUtils.ceil(sampleRate * BUFFER_MS_PER_PLAYBACK / MS_PER_SECOND);
	
		outSampleConverter = new Audio16BitSampleConverter();
		outFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
		                            sampleRate, 
		                            outSampleConverter.getBitsPerSample(), 
		                            2, 
		                            outSampleConverter.getBytesPerSample() * 2, 
		                            sampleRate, 
		                            outSampleConverter.isBigEndian());

		rootNode = null;
		nodeLock = new Object();

		running = starting = runPermanantly = false;
		
		hasStarted = false;
		bootLock = new Object();
	}
	
	@Override
	public void start() {
		hasStarted = true;
		
		super.start();
	}
	
	@Override
	public void run() {
		synchronized (bootLock) {
			running = true;
			starting = false;
		}
		
		try {
			sdl.open(outFormat);
		} catch (LineUnavailableException e) {
			return;
		}
		
		int outBufferSize = framesPerPlayback * outFormat.getFrameSize();
		byte[] blockOut = new byte[outBufferSize];
		
		// A single frame will contain samples for both channels.
		// NOTE: We reuse 'samples' for both channels.
		float[] samples = new float[framesPerPlayback];
		float[] accumulator = new float[framesPerPlayback * 2];

		sdl.start();

		boolean stopping = false;
		do {
			while (rootNode != null) {
				int bytesInBuffer = sdl.getBufferSize() - sdl.available();
				if (bytesInBuffer <= outBufferSize) {
					int framesRead = readAndProcessNodes(samples, accumulator);
					
					outSampleConverter.fromFloatSample(accumulator, blockOut, 0, accumulator.length);
					sdl.write(blockOut, 0, framesRead * outFormat.getFrameSize());
					
					Arrays.fill(accumulator, 0.0f);
				} else {
					int framesToSleep = (bytesInBuffer - outBufferSize) / outFormat.getFrameSize();
					long msToSleep = (long)(framesToSleep * MS_PER_SECOND / sampleRate);
					
					try {
						Thread.sleep(MathUtils.max(1L, msToSleep));
					} catch (InterruptedException e) {
					}
				}
			}
	
			sdl.drain();
			
			synchronized (bootLock) {
				boolean hasAwaitingNode = false;
				synchronized (nodeLock) {
					if (rootNode != null)
						hasAwaitingNode = true;
				}
				
				if (runPermanantly) {
					if (!hasAwaitingNode) {
						try {
							bootLock.wait();
						} catch (InterruptedException e) {
							stopping = true;
						}
					}
				} else {
					if (hasAwaitingNode) {
						stopping = true;
						running = false;
					}
				}
			}
		} while (!stopping);

		sdl.stop();
		sdl.close();
	}
	
	private int readAndProcessNodes(float[] samples, float[] accumulator) {
		AudioNode currentNode = rootNode;
		
		int framesRead = 0;
		while (currentNode != null) {
			if (currentNode.audioSource.isPlaying()) {
				int fr = currentNode.readAndProcessFrames(samples, accumulator);
				if (fr > framesRead)
					framesRead = fr;
			} else {
				removeNode(currentNode);
			}
			
			currentNode = currentNode.next;
		}
		
		return framesRead;
	}

	public boolean addAudioSource(AudioSource audioSource) {
		SoundFormat format = audioSource.getFormat();
		
		float sr = format.getSampleRate();
		if (!MathUtils.nearZero(sampleRate - sr)) {
			throw new IllegalSoundFormatException("Mismatching sample rate: " + sr + 
					"Hz, expected " + sampleRate + "Hz.");
		}
		
		SoundEncoding encoding = format.getEncoding();
		if (encoding != SoundEncoding.PCM_SIGNED)
			throw new IllegalSoundFormatException(encoding + " is not supported.");

		synchronized (bootLock) {
			addNode(new AudioNode(audioSource, framesPerPlayback));
		
			if (!running && !starting && hasStarted)
				return false;
		}
		
		if (!hasStarted) {
			starting = true;
			start();
		} else {
			synchronized (bootLock) {
				bootLock.notify();
			}
		}
		
		return true;
	}
	
	public boolean setRunPermanantly(boolean runPermantantly) {
		synchronized (bootLock) {
			if (!running && !starting && hasStarted)
				return false;
		
			this.runPermanantly = runPermantantly;
		}
		
		return true;
	}
	
	public boolean isRunPermantantly() {
		synchronized (bootLock) {
			return runPermanantly;
		}
	}
	
	public boolean hasStarted() {
		return hasStarted;
	}
	
	private void removeNode(AudioNode node) {
		synchronized (nodeLock) {
			if (node.prev != null)
				node.prev.next = node.next;
			if (node.next != null)
				node.next.prev = node.prev;
			
			if (node == rootNode)
				rootNode = node.next;
		}
	}
	
	private void addNode(AudioNode node) {
		synchronized (nodeLock) {
			node.next = rootNode;
			if (rootNode != null)
				rootNode.prev = node;
			
			rootNode = node;
		}
	}
	
	public float getSampleRate() {
		return sampleRate;
	}
	
	private static class AudioNode {
		
		private final AudioSource audioSource;
		private final int framesPerPlayback;
		
		private final SoundFormat format;
		private final ISampleConverter sampleConverter;
		private final byte[] sampleBuffer;

		private int prevSampleLatency;
		
		private AudioNode prev;
		private AudioNode next;
		
		private AudioNode(AudioSource audioSource, int framesPerPlayback) {
			this.audioSource = audioSource;
			this.framesPerPlayback = framesPerPlayback;

			format = audioSource.getFormat();
			sampleConverter = getAppropriateConverter(format);
			sampleBuffer = new byte[format.getFrameSize() * framesPerPlayback];

			prevSampleLatency = 0;
			
			prev = next = null;
		}
		
		public int readAndProcessFrames(float[] buffer, float[] accumulator) {
			int sampleLatency = audioSource.getSampleProcessingLatency();
			if (prevSampleLatency < sampleLatency) {
				// Convert from samples to frames. The frame size is
				// defined in bytes (including both channels), so we
				// have to also multiply the latency by the number of
				// channels.
				int byteLatency = 2 * (sampleLatency - prevSampleLatency);
				int frameLatency = (format.getChannels() * byteLatency) / format.getFrameSize();
				prevSampleLatency = sampleLatency;
				
				do {
					int framesToSkip = MathUtils.min(frameLatency, framesPerPlayback);
					
					int fs = audioSource.readRawFrames(sampleBuffer, framesToSkip);
					if (fs == -1)
						break;
					
					processSamples(sampleBuffer, buffer, null, AudioChannel.LEFT, fs);
					processSamples(sampleBuffer, buffer, null, AudioChannel.RIGHT, fs);
					
					frameLatency -= fs;
				} while (frameLatency != 0);
			}
			
			int fr = audioSource.readRawFrames(sampleBuffer, framesPerPlayback);
			if (fr == -1)
				return -1;
			
			processSamples(sampleBuffer, buffer, accumulator, AudioChannel.LEFT, fr);
			processSamples(sampleBuffer, buffer, accumulator, AudioChannel.RIGHT, fr);
		
			return fr;
		}

		private void processSamples(byte[] samples, float[] buffer, float[] accumulator, AudioChannel channel, int numSamples) {
			sampleConverter.toFloatSample(samples, buffer, getSampleOffset(channel), numSamples);
			audioSource.preProcess(buffer, numSamples, channel);
			
			if (accumulator != null) {
				int ai = (channel == AudioChannel.LEFT) ? 0 : 1;
				for (int i = 0; i < numSamples; i++, ai += 2)
					accumulator[ai] += buffer[i];
			}
		}
		
		private int getSampleOffset(AudioChannel channel) {
			if (format.getChannels() <= 1)
				return 0;
			return (channel == AudioChannel.LEFT) ? 0 : 1;
		}
		
		private static ISampleConverter getAppropriateConverter(SoundFormat format) {
			int bytesPerSample = (format.getBitsPerSample() + 7) / 8;
			int sampleStepSize = format.getChannels();
			
			switch (bytesPerSample) {
			case NUM_BYTES_FOR_8BIT:
				return new Audio8BitSampleConverter(format.getBitsPerSample(), 
				                                    sampleStepSize,
				                                    format.isLeftShifted());
			case NUM_BYTES_FOR_16BIT:
				return new Audio16BitSampleConverter(format.getBitsPerSample(), 
				                                     sampleStepSize, 
				                                     format.isBigEndian(),
				                                     format.isLeftShifted());
			case NUM_BYTES_FOR_24BIT:
				return new Audio24BitSampleConverter(format.getBitsPerSample(),
				                                     sampleStepSize, 
				                                     format.isBigEndian(),
				                                     format.isLeftShifted());
			case NUM_BYTES_FOR_32BIT:
				return new Audio32BitSampleConverter(format.getBitsPerSample(), 
				                                     sampleStepSize, 
				                                     format.isBigEndian(),
				                                     format.isLeftShifted());
			default:
				throw new RuntimeException("Unsupported sample size: " + format.getBitsPerSample());
			}
		}
	}
}
