package com.g4mesoft.sound.processor;

import com.g4mesoft.math.Vec3f;
import com.g4mesoft.sound.format.AudioFile;

public class AudioSource extends AudioLine {

	public static final int LOOP_CONTINOUSLY = -1;
	
	private VolumeAudioProcessor volumeProcessor;
	
	private int frameLocation;
	private int loopAmount;
	
	public final AudioFile audioFile;

	public AudioSource(AudioFile audioFile, Vec3f pos) {
		super(pos);
		
		this.audioFile = audioFile;
		
		frameLocation = 0;
		loopAmount = 0;
	}

	public AudioSource setVolume(float volume) {
		if (volumeProcessor == null) {
			addPreProcessor(volumeProcessor = new VolumeAudioProcessor(volume));
		} else {
			volumeProcessor.setVolume(volume);
		}

		return this;
	}

	public int readFrames(byte[] block, int framesToRead) {
		if (frameLocation == -1)
			return 0;
		
		int frameSize = audioFile.getFormat().getFrameSize();
		
		int bytesToRead = framesToRead * frameSize;
		int bp = frameLocation * frameSize;
		
		while (true) {
			int br = audioFile.getData(block, bp, 0, bytesToRead);
			bytesToRead -= br;
			bp += br;

			frameLocation = bp / frameSize;

			if (bytesToRead <= 0)
				break;
			
			if (frameLocation >= audioFile.getLengthInFrames()) {
				if (loopAmount != 0) {
					if (loopAmount != LOOP_CONTINOUSLY)
						loopAmount--;
					frameLocation = bp = 0;
				} else {
					frameLocation = -1;
					break;
				}
			}
		}
		
		return framesToRead - bytesToRead / frameSize;
	}
	
	public void setFrameLocation(int nFrameLocation) {
		frameLocation = nFrameLocation;
	}

	public void setLoopAmount(int nLoopAmount) {
		loopAmount = nLoopAmount;
	}

	public void stop() {
		frameLocation = -1;
	}
	
	public boolean isPlaying() {
		return frameLocation != -1;
	}
}
