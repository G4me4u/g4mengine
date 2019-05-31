package com.g4mesoft.sound.processor;

import com.g4mesoft.math.MathUtils;
import com.g4mesoft.sound.analysis.FastFourierTransform;

public class PitchAudioProcessor implements IAudioProcessor {

	private static final int FRAME_SIZE = 1024;
	private static final int HALF_FRAME_SIZE = FRAME_SIZE / 2;
	private static final int OVERLAP_FACTOR = 8;
	private static final int STEP_SIZE = FRAME_SIZE / OVERLAP_FACTOR;
	private static final int SAMPLES_LATENCY = FRAME_SIZE - STEP_SIZE;

	private static final float EXPECTED_PHASE = 2.0f * MathUtils.PI / OVERLAP_FACTOR;
	
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
	
	@Override
	public int getSampleLatency() {
		return SAMPLES_LATENCY;
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
				samples[i] = MathUtils.clamp(sample, -1.0f, 1.0f);

				if (++samplesRead < FRAME_SIZE)
					continue;
				samplesRead = SAMPLES_LATENCY;

				for (int k = 0; k < FRAME_SIZE; k++) {
					fftSamples[(k << 1) + 0] = samplesIn[k] * WINDOW_FUNC[k];
					fftSamples[(k << 1) + 1] = 0.0f;
				}

				FastFourierTransform.forwardTransform(fftSamples);

				for (int k = 0; k < HALF_FRAME_SIZE; k++) {
					float real = fftSamples[(k << 1) + 0];
					float imag = fftSamples[(k << 1) + 1];
					float phase = MathUtils.atan2(imag, real);
					
					float tmp = phase - lastPhase[k];
					lastPhase[k] = phase;
					
					int index = (int)(k * pitch);
					if (index >= HALF_FRAME_SIZE)
						break;
					
					float magn = MathUtils.sqrt(real * real + imag * imag);
					if (magn > synMagn[index]) {
						tmp -= (double)k * EXPECTED_PHASE;
						
						long qpd = (long)(tmp / MathUtils.PI);
						tmp -= MathUtils.PI * (qpd + (qpd < 0 ? (qpd & 1L) : -(qpd & 1L)));
						
						tmp *= OVERLAP_FACTOR / (2.0f * MathUtils.PI);
						tmp = k * freqPerBin + tmp * freqPerBin;

						synFreq[index] = tmp * pitch;
					}
					synMagn[index] += magn;
				}

				for (int k = 0; k < HALF_FRAME_SIZE; k++) {
					float magn = synMagn[k];
					float tmp = synFreq[k];
					synMagn[k] = 0.0f;
					synFreq[k] = 0.0f;
					
					tmp -= freqPerBin * k;
					tmp /= freqPerBin;
					tmp *= 2.0f * MathUtils.PI / OVERLAP_FACTOR;
					tmp += k * EXPECTED_PHASE;
					
					sumPhase[k] += tmp;
					sumPhase[k] %= MathUtils.PI * 2.0f;
					float phase = sumPhase[k];

					fftSamples[(k << 1) + 0] = magn * MathUtils.cos(phase);
					fftSamples[(k << 1) + 1] = magn * MathUtils.sin(phase);
					
					fftSamples[(k << 1) + FRAME_SIZE + 0] = 0.0f;
					fftSamples[(k << 1) + FRAME_SIZE + 1] = 0.0f;
				} 

				FastFourierTransform.inverseTransform(fftSamples);

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
			WINDOW_FUNC[i] = 0.5f - 0.5f * MathUtils.cos(2.0f * MathUtils.PI * i / FRAME_SIZE);
	}
}
