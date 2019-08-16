package com.g4mesoft.sound.convert;

import com.g4mesoft.util.MemoryUtil;

public class Audio16BitSampleConverter extends AbstractSampleConverter {

	private static final int SAMPLE_SIZE_IN_BYTES = 2;

	public Audio16BitSampleConverter() {
		this(SAMPLE_SIZE_IN_BYTES * BYTE_SIZE_IN_BITS);
	}

	public Audio16BitSampleConverter(int bitsPerSample) {
		super(bitsPerSample);
	}

	public Audio16BitSampleConverter(int bitsPerSample, int sampleStepSize) {
		super(bitsPerSample, sampleStepSize);
	}

	public Audio16BitSampleConverter(int bitsPerSample, boolean bigEndian) {
		super(bitsPerSample, bigEndian);
	}

	public Audio16BitSampleConverter(int sampleSizeInBits, int sampleStepSize, boolean bigEndian) {
		super(sampleSizeInBits, sampleStepSize, bigEndian);
	}

	public Audio16BitSampleConverter(int sampleSizeInBits, int sampleStepSize, boolean bigEndian, boolean leftShifted) {
		super(sampleSizeInBits, sampleStepSize, bigEndian, leftShifted);
	}

	@Override
	public int readSampleLittleEndian(byte[] buffer, int sampleIndex) {
		return MemoryUtil.littleEndianToShortUnsignedInt(buffer, sampleIndex << 1);
	}

	@Override
	public int readSampleBigEndian(byte[] buffer, int sampleIndex) {
		return MemoryUtil.bigEndianToShortUnsignedInt(buffer, sampleIndex << 1);
	}

	@Override
	public void writeSampleLittleEndian(int sample, byte[] buffer, int sampleIndex) {
		MemoryUtil.writeLittleEndianShort(buffer, (short)sample, sampleIndex << 1);
	}

	@Override
	public void writeSampleBigEndian(int sample, byte[] buffer, int sampleIndex) {
		MemoryUtil.writeBigEndianShort(buffer, (short)sample, sampleIndex << 1);
	}

	@Override
	public int getBytesPerSample() {
		return SAMPLE_SIZE_IN_BYTES;
	}
}
