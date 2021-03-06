package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.g4mesoft.sound.convert.Audio16BitSampleConverter;
import com.g4mesoft.sound.convert.ISampleConverter;
import com.g4mesoft.sound.format.AudioBitInputStream;
import com.g4mesoft.sound.format.SoundEncoding;
import com.g4mesoft.sound.format.SoundFormat;

public class MPEGFrameDecoder {

	/**
	 * The number of frames that have to be decoded before they're processed. To
	 * better help the validation process these frames will also be checked for
	 * whether the format is consistent throughout all the frames. If this is
	 * not the case and both frames are valid, then the MPEG is considered
	 * invalid.
	 */
	private static final int FRAME_CACHE_COUNT = 4;
	
	/**
	 * The number of channels in the output sample array.
	 */
	private static final int NUM_CHANNELS_OUT = 2;
	
	private int numCachedSamples;
	private final MPEGFrame[] frameCache;
	
	private int numValidFrames;
	private int currentFrame;

	private boolean commonInfoSet;
	private int commonVersion;
	private int commonLayer;
	private int commonBitrate;
	private int commonFrequency;

	private MPEGSynthesisSubbandFilter synthesisFilter;
	
	private List<byte[]> bufferedSamples;
	private int numBufferedBytes;
	
	private final ISampleConverter sampleConverter;
	
	public MPEGFrameDecoder() {
		frameCache = new MPEGFrame[FRAME_CACHE_COUNT];
		for (int i = 0; i < FRAME_CACHE_COUNT; i++)
			frameCache[i] = new MPEGFrame(this);
		currentFrame = 0;
		
		commonInfoSet = false;
		synthesisFilter = null;
		
		bufferedSamples = new ArrayList<byte[]>();
		numBufferedBytes = 0;
		
		sampleConverter = new Audio16BitSampleConverter();
	}

	public boolean readNextFrame(AudioBitInputStream abis) throws IOException {
		MPEGFrame frame = frameCache[currentFrame];
		
		while (true) {
			if (!frame.readFrameHeader(abis))
				return false;
			
			if (commonInfoSet && !checkCommonInfo(frame)) {
				frame.restoreFrame(abis, 1);
			} else {
				// Preload the frame (single read optimization)
				abis.prereadBytes(frame.accumulatedSize);
			
				try {
					frame.readAudioData(abis);
					numValidFrames++;
				} catch (CorruptedMPEGFrameException e) {
					// We've read the frame header successfully.
					// We assume there was supposed to be a valid
					// frame at this location but it was corrupted.

					if (numValidFrames == 0)
						continue;
					
					frame.silenceFrame();
				}

				numCachedSamples += frame.getNumSamples();
				break;
			}
		}
		
		if (!commonInfoSet) {
			setCommonInfo(frame);
			commonInfoSet = true;
		}
		
		currentFrame++;
		
		if (currentFrame >= FRAME_CACHE_COUNT) {
			flushCachedSamples();
			currentFrame = 0;
		}
		
		return true;
	}

	private boolean checkCommonInfo(MPEGFrame frame) {
		MPEGHeader header = frame.getHeader();
		if (header.version != commonVersion)
			return false;
		if (header.layer != commonLayer)
			return false;
		
		if (frame.bitrate != commonBitrate)
			return false;
		if (frame.frequency != commonFrequency)
			return false;
		
		return true;
	}

	private void setCommonInfo(MPEGFrame frame) {
		MPEGHeader header = frame.getHeader();
		commonVersion = header.version;
		commonLayer = header.layer;
		
		commonBitrate = frame.bitrate;
		commonFrequency = frame.frequency;
	}
	
	public void flushCachedSamples() {
		if (currentFrame <= 0)
			return;
		
		byte[] samples = new byte[numCachedSamples * 2];

		int offset = 0;
		for (int i = 0; i < currentFrame; i++) {
			float[] buffer = frameCache[i].getSamples();
			sampleConverter.fromFloatSample(buffer, samples, offset, buffer.length);
			offset += buffer.length;
		}

		bufferedSamples.add(samples);
		numBufferedBytes += samples.length;
		numCachedSamples = 0;
	}
	
	public MPEGSynthesisSubbandFilter getSynthesisSubbandFilter() {
		if (synthesisFilter == null)
			synthesisFilter = new MPEGSynthesisSubbandFilter();
		return synthesisFilter;
	}

	public int getNumValidFrames() {
		return numValidFrames;
	}

	public byte[] getCompiledSamples() {
		byte[] data = new byte[numBufferedBytes];
		
		int offset = 0;
		for (byte[] bufferedData : bufferedSamples) {
			System.arraycopy(bufferedData, 0, data, offset, bufferedData.length);
			offset += bufferedData.length;
		}
		
		bufferedSamples.clear();
		numBufferedBytes = 0;
		
		return data;
	}
	
	public SoundFormat getFormat() {
		return new SoundFormat(SoundEncoding.PCM_SIGNED, 
		                       commonFrequency,
		                       sampleConverter.getBitsPerSample(),
		                       sampleConverter.isLeftShifted(),
		                       sampleConverter.isBigEndian(),
		                       sampleConverter.getBytesPerSample() * NUM_CHANNELS_OUT,
		                       NUM_CHANNELS_OUT);
	}
}
