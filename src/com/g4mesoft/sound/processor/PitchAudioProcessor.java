package com.g4mesoft.sound.processor;

import com.g4mesoft.sound.analysis.FastFourierTransform;

public class PitchAudioProcessor implements AudioProcessor {

	private static final float PIF = 3.14159265358979f;
	
	private static final int FRAME_SIZE = 1024;
	private static final int HALF_FRAME_SIZE = FRAME_SIZE / 2;
	private static final int OVERLAP_FACTOR = 8;
	private static final int STEP_SIZE = FRAME_SIZE / OVERLAP_FACTOR;
	private static final int SAMPLES_LATENCY = FRAME_SIZE - STEP_SIZE;

	private static final float EXPECTED_PHASE = 2.0f * PIF * STEP_SIZE / FRAME_SIZE;
	
	private static final float[] WINDOW_FUNC;
	
	private float pitch;
	private final float freqPerBin;
	
	private final PitchShifter[] pitchShifters;
	
	public PitchAudioProcessor(float sampleRate) {
		this(1.0f, sampleRate);
	}
	
	public PitchAudioProcessor(float pitch, float sampleRate) {
		this.pitch = pitch;
		this.freqPerBin = sampleRate / FRAME_SIZE;
		
		pitchShifters = new PitchShifter[2];
		pitchShifters[0] = new PitchShifter();
		pitchShifters[1] = new PitchShifter();
	}
	
	@Override
	public void process(float[] samples, int numSamples, AudioChannel channel) {
		pitchShifters[channel.index].process(samples, numSamples);
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	private class PitchShifter {
		
		private final float[] samplesIn;
		private final float[] outputAccum;
		
		private final float[] fftSamples;
		
		private final float[] lastPhase;
		private final float[] sumPhase;
		
		private final float[] synMagn;
		private final float[] synFreq;
		
		private int samplesRead;
		
		public PitchShifter() {
			samplesIn = new float[FRAME_SIZE];
			outputAccum = new float[FRAME_SIZE + STEP_SIZE];
		
			fftSamples = new float[FRAME_SIZE * 2];
			
			lastPhase = new float[HALF_FRAME_SIZE];
			sumPhase = new float[HALF_FRAME_SIZE];
			
			synMagn = new float[HALF_FRAME_SIZE];
			synFreq = new float[HALF_FRAME_SIZE];
	
			samplesRead = SAMPLES_LATENCY;
		}
		
		public void process(float[] samples, int numSamples) {
			for (int i = 0; i < numSamples; i++) {
				samplesIn[samplesRead] = samples[i];
				float sample = outputAccum[samplesRead - SAMPLES_LATENCY];
				samples[i] = sample > 1.0f ? 1.0f : (sample < -1.0f ? -1.0f : sample);

				if (++samplesRead < FRAME_SIZE)
					continue;
				samplesRead = SAMPLES_LATENCY;

				for (int k = 0; k < FRAME_SIZE; k++) {
					fftSamples[(k << 1) + 0] = samplesIn[k] * WINDOW_FUNC[k];
					fftSamples[(k << 1) + 1] = 0.0f;
				}

				FastFourierTransform.transform(fftSamples, -1.0f);

				for (int k = 0; k < HALF_FRAME_SIZE; k++) {
					float real = fftSamples[(k << 1) + 0];
					float imag = fftSamples[(k << 1) + 1];
					float phase = (float)Math.atan2(imag, real);
					
					float tmp = phase - lastPhase[k];
					lastPhase[k] = phase;

					int index = (int)(k * pitch);
					if (index < HALF_FRAME_SIZE) {
						tmp -= (double)k * EXPECTED_PHASE;

						long qpd = (long)(tmp / PIF);
						tmp -= PIF * (qpd + (qpd > 0 ? (qpd & 1) : -(qpd & 1)));

						tmp *= OVERLAP_FACTOR / (2.0f * PIF);
						tmp = k * freqPerBin + tmp * freqPerBin;
						
						synMagn[index] += (float)Math.sqrt(real * real + imag * imag);
						synFreq[index] = tmp * pitch;
					}
				}

				for (int k = 0; k < HALF_FRAME_SIZE; k++) {
					float magn = synMagn[k];
					float tmp = synFreq[k];
					synMagn[k] = 0.0f;
					synFreq[k] = 0.0f;
					
					tmp -= freqPerBin * k;
					tmp /= freqPerBin;
					tmp *= 2.0f * PIF / OVERLAP_FACTOR;
					tmp += k * EXPECTED_PHASE;
					
					float phase = (sumPhase[k] += tmp);

					fftSamples[(k << 1) + 0] = magn * (float)Math.cos(phase);
					fftSamples[(k << 1) + 1] = magn * (float)Math.sin(phase);
					
					fftSamples[(k << 1) + FRAME_SIZE + 0] = 0.0f;
					fftSamples[(k << 1) + FRAME_SIZE + 1] = 0.0f;
				} 

				FastFourierTransform.transform(fftSamples, 1.0f);

				for(int k = STEP_SIZE; k < FRAME_SIZE; k++) {
					sample = 2.0f * WINDOW_FUNC[k] * fftSamples[k << 1] / (HALF_FRAME_SIZE * OVERLAP_FACTOR);
					outputAccum[k - STEP_SIZE] = outputAccum[k] + sample;
					samplesIn[k - STEP_SIZE] = samplesIn[k];
				}
			}
		}
	}
	
	static {
		WINDOW_FUNC = new float[FRAME_SIZE];
		for (int i = 0; i < FRAME_SIZE; i++) 
			WINDOW_FUNC[i] = 0.5f - 0.5f * (float)Math.cos(2.0f * Math.PI * i / FRAME_SIZE);
	}
}
