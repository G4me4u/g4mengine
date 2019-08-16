package com.g4mesoft.sound.convert;

import com.g4mesoft.util.MemoryUtil;

public class Audio24BitSampleConverter extends AbstractSampleConverter {

	private static final int SAMPLE_SIZE_IN_BYTES = 3;

	public Audio24BitSampleConverter() {
		this(SAMPLE_SIZE_IN_BYTES * BYTE_SIZE_IN_BITS);
	}
	
	public Audio24BitSampleConverter(int bitsPerSample) {
		super(bitsPerSample);
	}

	public Audio24BitSampleConverter(int bitsPerSample, int sampleStepSize) {
		super(bitsPerSample, sampleStepSize);
	}

	public Audio24BitSampleConverter(int bitsPerSample, boolean bigEndian) {
		super(bitsPerSample, bigEndian);
	}

	public Audio24BitSampleConverter(int sampleSizeInBits, int sampleStepSize, boolean bigEndian) {
		super(sampleSizeInBits, sampleStepSize, bigEndian);
	}
	
	public Audio24BitSampleConverter(int sampleSizeInBits, int sampleStepSize, boolean bigEndian, boolean leftShifted) {
		super(sampleSizeInBits, sampleStepSize, bigEndian, leftShifted);
	}
	
	@Override
	public int readSampleLittleEndian(byte[] buffer, int sampleIndex) {
		return MemoryUtil.littleEndianTo24BitUnsignedInt(buffer, sampleIndex * SAMPLE_SIZE_IN_BYTES);
	}

	@Override
	public int readSampleBigEndian(byte[] buffer, int sampleIndex) {
		return MemoryUtil.bigEndianTo24BitUnsignedInt(buffer, sampleIndex * SAMPLE_SIZE_IN_BYTES);
	}

	@Override
	public void writeSampleLittleEndian(int sample, byte[] buffer, int sampleIndex) {
		MemoryUtil.writeLittleEndian24Bit(buffer, sample, sampleIndex * SAMPLE_SIZE_IN_BYTES);
	}

	@Override
	public void writeSampleBigEndian(int sample, byte[] buffer, int sampleIndex) {
		MemoryUtil.writeBigEndian24Bit(buffer, sample, sampleIndex * SAMPLE_SIZE_IN_BYTES);
	}

	@Override
	public int getBytesPerSample() {
		return SAMPLE_SIZE_IN_BYTES;
	}
}
