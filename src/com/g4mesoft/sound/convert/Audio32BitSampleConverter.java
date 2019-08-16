package com.g4mesoft.sound.convert;

import com.g4mesoft.util.MemoryUtil;

public class Audio32BitSampleConverter extends AbstractSampleConverter {

	private static final int SAMPLE_SIZE_IN_BYTES = 4;

	public Audio32BitSampleConverter() {
		this(SAMPLE_SIZE_IN_BYTES * BYTE_SIZE_IN_BITS);
	}
	
	public Audio32BitSampleConverter(int bitsPerSample) {
		super(bitsPerSample);
	}

	public Audio32BitSampleConverter(int bitsPerSample, int sampleStepSize) {
		super(bitsPerSample, sampleStepSize);
	}

	public Audio32BitSampleConverter(int bitsPerSample, boolean bigEndian) {
		super(bitsPerSample, bigEndian);
	}

	public Audio32BitSampleConverter(int sampleSizeInBits, int sampleStepSize, boolean bigEndian) {
		super(sampleSizeInBits, sampleStepSize, bigEndian);
	}
	
	public Audio32BitSampleConverter(int sampleSizeInBits, int sampleStepSize, boolean bigEndian, boolean leftShifted) {
		super(sampleSizeInBits, sampleStepSize, bigEndian, leftShifted);
	}
	
	@Override
	public int readSampleLittleEndian(byte[] buffer, int sampleIndex) {
		return MemoryUtil.littleEndianToInt(buffer, sampleIndex << 2);
	}

	@Override
	public int readSampleBigEndian(byte[] buffer, int sampleIndex) {
		return MemoryUtil.bigEndianToInt(buffer, sampleIndex << 2);
	}

	@Override
	public void writeSampleLittleEndian(int sample, byte[] buffer, int sampleIndex) {
		MemoryUtil.writeLittleEndianInt(buffer, (short)sample, sampleIndex << 2);
	}

	@Override
	public void writeSampleBigEndian(int sample, byte[] buffer, int sampleIndex) {
		MemoryUtil.writeBigEndianInt(buffer, (short)sample, sampleIndex << 2);
	}

	@Override
	public int getBytesPerSample() {
		return SAMPLE_SIZE_IN_BYTES;
	}
}
