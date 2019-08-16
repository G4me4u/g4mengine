package com.g4mesoft.sound.convert;

public class Audio8BitSampleConverter extends AbstractSampleConverter {

	private static final int SAMPLE_SIZE_IN_BYTES = 1;

	public Audio8BitSampleConverter() {
		this(SAMPLE_SIZE_IN_BYTES * BYTE_SIZE_IN_BITS);
	}

	public Audio8BitSampleConverter(int bitsPerSample) {
		super(bitsPerSample);
	}

	public Audio8BitSampleConverter(int bitsPerSample, int sampleStepSize) {
		super(bitsPerSample, sampleStepSize);
	}

	public Audio8BitSampleConverter(int bitsPerSample, int sampleStepSize, boolean leftShifted) {
		super(bitsPerSample, sampleStepSize, DEFAULT_BIG_ENDIAN, leftShifted);
	}

	@Override
	public int readSampleLittleEndian(byte[] buffer, int sampleIndex) {
		return buffer[sampleIndex] & 0xFF;
	}

	@Override
	public int readSampleBigEndian(byte[] buffer, int sampleIndex) {
		return readSampleLittleEndian(buffer, sampleIndex);
	}

	@Override
	public void writeSampleLittleEndian(int sample, byte[] buffer, int sampleIndex) {
		buffer[sampleIndex] = (byte)sample;
	}

	@Override
	public void writeSampleBigEndian(int sample, byte[] buffer, int sampleIndex) {
		writeSampleLittleEndian(sample, buffer, sampleIndex);
	}

	@Override
	public int getBytesPerSample() {
		return SAMPLE_SIZE_IN_BYTES;
	}
}
