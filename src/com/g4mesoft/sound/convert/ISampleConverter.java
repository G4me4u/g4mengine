package com.g4mesoft.sound.convert;

public interface ISampleConverter {

	public void toFloatSample(byte[] input, float[] output, int sampleOffset, int numSamples);

	public void fromFloatSample(float[] input, byte[] output, int sampleOffset, int numSamples);
	
	public int getBitsPerSample();

	public int getBytesPerSample();
	
	public boolean isBigEndian();

	public boolean isLeftShifted();
	
}
