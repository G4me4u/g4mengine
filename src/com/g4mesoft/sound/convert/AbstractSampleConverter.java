package com.g4mesoft.sound.convert;

import com.g4mesoft.math.MathUtils;

public abstract class AbstractSampleConverter implements ISampleConverter {

	protected static final int DEFAULT_SAMPLE_STEP_SIZE = 1;
	protected static final boolean DEFAULT_BIG_ENDIAN = false;
	protected static final boolean DEFAULT_LEFT_SHIFTED = false;
	
	protected static final int BYTE_SIZE_IN_BITS = 8;
	protected static final int INT_SIZE_IN_BITS = 4 * BYTE_SIZE_IN_BITS;
	
	protected static final int MINIMUM_BITS_PER_SAMPLE = 1;
	protected static final int MAXIMUM_BITS_PER_SAMPLE = 32;
	
	private static final long[] SAMPLE_SCALARS = {
		0, 0x00000001L, 0x00000002L, 0x00000004L, 0x00000008L,
		   0x00000010L, 0x00000020L, 0x00000040L, 0x00000080L,
		   0x00000100L, 0x00000200L, 0x00000400L, 0x00000800L,
		   0x00001000L, 0x00002000L, 0x00004000L, 0x00008000L,
		   0x00010000L, 0x00020000L, 0x00040000L, 0x00080000L,
		   0x00100000L, 0x00200000L, 0x00400000L, 0x00800000L,
		   0x01000000L, 0x02000000L, 0x04000000L, 0x08000000L,
		   0x10000000L, 0x20000000L, 0x40000000L, 0x80000000L
	};
	
	private static final int[] SAMPLE_SIGN_BIT = {
		0, 0x00000001, 0x00000002, 0x00000004, 0x00000008,
		   0x00000010, 0x00000020, 0x00000040, 0x00000080,
		   0x00000100, 0x00000200, 0x00000400, 0x00000800,
		   0x00001000, 0x00002000, 0x00004000, 0x00008000,
		   0x00010000, 0x00020000, 0x00040000, 0x00080000,
		   0x00100000, 0x00200000, 0x00400000, 0x00800000,
		   0x01000000, 0x02000000, 0x04000000, 0x08000000,
		   0x10000000, 0x20000000, 0x40000000, 0x80000000
	};
	
	private static final int[] SAMPLE_PADDING = {
		0xFFFFFFFF, 0xFFFFFFFE, 0xFFFFFFFC, 0xFFFFFFF8,
		0xFFFFFFF0, 0xFFFFFFE0, 0xFFFFFFC0, 0xFFFFFF80,
		0xFFFFFF00, 0xFFFFFE00, 0xFFFFFC00, 0xFFFFF800,
		0xFFFFF000, 0xFFFFE000, 0xFFFFC000, 0xFFFF8000,
		0xFFFF0000, 0xFFFE0000, 0xFFFC0000, 0xFFF80000,
		0xFFF00000, 0xFFE00000, 0xFFC00000, 0xFF800000,
		0xFF000000, 0xFE000000, 0xFC000000, 0xF8000000,
		0xF0000000, 0xE0000000, 0xC0000000, 0x80000000, 0
	};
	
	private final int bitsPerSample;
	private final int sampleStepSize;
	private final boolean bigEndian;
	private final boolean leftShifted;
	
	private final int sampleShifting;
	private final double sampleScalar;

	private final int sampleSign;
	
	private final int samplePadding;
	private final int sampleMask;

	private final int minSample;
	private final int maxSample;
	
	public AbstractSampleConverter(int bitsPerSample) {
		this(bitsPerSample, DEFAULT_SAMPLE_STEP_SIZE, DEFAULT_BIG_ENDIAN, DEFAULT_LEFT_SHIFTED);
	}

	public AbstractSampleConverter(int bitsPerSample, int sampleStepSize) {
		this(bitsPerSample, sampleStepSize, DEFAULT_BIG_ENDIAN, DEFAULT_LEFT_SHIFTED);
	}

	public AbstractSampleConverter(int bitsPerSample, boolean bigEndian) {
		this(bitsPerSample, DEFAULT_SAMPLE_STEP_SIZE, bigEndian, DEFAULT_LEFT_SHIFTED);
	}

	public AbstractSampleConverter(int bitsPerSample, int sampleStepSize, boolean bigEndian) {
		this(bitsPerSample, sampleStepSize, bigEndian, DEFAULT_LEFT_SHIFTED);
	}
	
	public AbstractSampleConverter(int bitsPerSample, int sampleStepSize, boolean bigEndian, boolean leftShifted) {
		if (bitsPerSample < MINIMUM_BITS_PER_SAMPLE)
			throw new IllegalArgumentException(bitsPerSample + " < " + MINIMUM_BITS_PER_SAMPLE);
		if (bitsPerSample > MAXIMUM_BITS_PER_SAMPLE)
			throw new IllegalArgumentException(bitsPerSample + " > " + MAXIMUM_BITS_PER_SAMPLE);
		
		int maximumSampleSize = getBytesPerSample() * BYTE_SIZE_IN_BITS;
		if (bitsPerSample > maximumSampleSize)
			throw new IllegalArgumentException(bitsPerSample + " > " + maximumSampleSize);
		
		if (sampleStepSize <= 0)
			throw new IllegalArgumentException("sampleStepSize <= 0");
		
		this.bitsPerSample = bitsPerSample;
		this.sampleStepSize = sampleStepSize;
		this.bigEndian = bigEndian;
		this.leftShifted = leftShifted;
		
		sampleShifting = leftShifted ? maximumSampleSize - bitsPerSample : 0;
		sampleScalar = SAMPLE_SCALARS[bitsPerSample];

		sampleSign = SAMPLE_SIGN_BIT[bitsPerSample];

		samplePadding = SAMPLE_PADDING[bitsPerSample];
		sampleMask = ~samplePadding;
		
		minSample = sampleSign | samplePadding;
		maxSample = ~minSample;
	}

	@Override
	public void toFloatSample(byte[] input, float[] output, int sampleOffset, int numSamples) {
		int sampleIndex = sampleOffset;
		
		if (bigEndian) {
			for (int i = 0; i < numSamples; i++, sampleIndex += sampleStepSize) {
				int sample = readSampleBigEndian(input, sampleIndex) >>> sampleShifting;
				if ((sample & sampleSign) != 0)
					sample |= samplePadding;
				
				output[i] = (float)(sample / sampleScalar);
			}
		} else {
			for (int i = 0; i < numSamples; i++, sampleIndex += sampleStepSize) {
				int sample = readSampleLittleEndian(input, sampleIndex) >>> sampleShifting;
				if ((sample & sampleSign) != 0)
					sample |= samplePadding;

				output[i] = (float)(sample / sampleScalar);
			}
		}
	}

	@Override
	public void fromFloatSample(float[] input, byte[] output, int sampleOffset, int numSamples) {
		int sampleIndex = sampleOffset;

		if (bigEndian) {
			for (int i = 0; i < numSamples; i++, sampleIndex += sampleStepSize) {
				int sample = MathUtils.clamp((int)(input[i] * sampleScalar), minSample, maxSample);
				writeSampleBigEndian((sample & sampleMask) << sampleShifting, output, sampleIndex);
			}
		} else {
			for (int i = 0; i < numSamples; i++, sampleIndex += sampleStepSize) {
				int sample = MathUtils.clamp((int)(input[i] * sampleScalar), minSample, maxSample);
				writeSampleLittleEndian((sample & sampleMask) << sampleShifting, output, sampleIndex);
			}
		}
	}
	
	@Override
	public int getBitsPerSample() {
		return bitsPerSample;
	}

	@Override
	public boolean isBigEndian() {
		return bigEndian;
	}

	@Override
	public boolean isLeftShifted() {
		return leftShifted;
	}
	
	public abstract int readSampleLittleEndian(byte[] buffer, int sampleIndex);

	public abstract int readSampleBigEndian(byte[] buffer, int sampleIndex);

	public abstract void writeSampleLittleEndian(int sample, byte[] buffer, int sampleIndex);

	public abstract void writeSampleBigEndian(int sample, byte[] buffer, int sampleIndex);
	
}
