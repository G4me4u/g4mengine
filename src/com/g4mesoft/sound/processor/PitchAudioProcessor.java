package com.g4mesoft.sound.processor;

import com.g4mesoft.math.MathUtils;
import com.g4mesoft.sound.analysis.FastFourierTransform;

public class PitchAudioProcessor implements IAudioProcessor {

	private static final int FRAME_SIZE = 2048;
	private static final int HALF_FRAME_SIZE = FRAME_SIZE / 2;
	private static final int OVERLAP_FACTOR = 8;
	private static final int STEP_SIZE = FRAME_SIZE / OVERLAP_FACTOR;
	private static final int SAMPLES_LATENCY = FRAME_SIZE - STEP_SIZE;

	private static final double EXPECTED_PHASE = 2.0 * MathUtils.PI_D / OVERLAP_FACTOR;
	
	private static final float[] WINDOW_FUNC;
	
	private float pitch;
	private final double freqPerBin;
	
	private final PitchShifter[] pitchShifters;
	
	public PitchAudioProcessor(float sampleRate) {
		this(1.0f, sampleRate);
	}
	
	public PitchAudioProcessor(float pitch, float sampleRate) {
		this.pitch = pitch;
		this.freqPerBin = (double)sampleRate / FRAME_SIZE;
		
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
		
		private final double[] lastPhase;
		private final double[] sumPhase;
		
		private final float[] synMagn;
		private final float[] synFreq;
		
		private int samplesRead;
		
		public PitchShifter() {
			samplesIn = new float[FRAME_SIZE];
			outputAccum = new float[FRAME_SIZE + STEP_SIZE];
		
			fftSamples = new float[FRAME_SIZE * 2];
			
			lastPhase = new double[HALF_FRAME_SIZE];
			sumPhase = new double[HALF_FRAME_SIZE];
			
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
					double real = fftSamples[(k << 1) + 0];
					double imag = fftSamples[(k << 1) + 1];
					double phase = MathUtils.atan2(imag, real);
					
					double tmp = phase - lastPhase[k];
					lastPhase[k] = phase;
					
					int index = (int)(k * pitch);
					if (index >= HALF_FRAME_SIZE)
						break;
					
					float magn = (float)MathUtils.sqrt(real * real + imag * imag);
					if (magn > synMagn[index]) {
						tmp -= (double)k * EXPECTED_PHASE;
						
						long qpd = (long)(tmp / MathUtils.PI_D);
						tmp -= MathUtils.PI_D * (qpd + (qpd > 0 ? (qpd & 1L) : -(qpd & 1L)));
						
						tmp *= OVERLAP_FACTOR / (2.0 * MathUtils.PI_D);
						tmp = k * freqPerBin + tmp * freqPerBin;

						synFreq[index] = (float)(tmp * pitch);
					}
					
					synMagn[index] += magn;
				}

				for (int k = 0; k < HALF_FRAME_SIZE; k++) {
					float magn = synMagn[k];
					double tmp = synFreq[k];
					synMagn[k] = 0.0f;
					synFreq[k] = 0.0f;
					
					tmp -= freqPerBin * k;
					tmp /= freqPerBin;
					tmp *= 2.0 * MathUtils.PI_D / OVERLAP_FACTOR;
					tmp += k * EXPECTED_PHASE;
					
					sumPhase[k] += tmp;
					sumPhase[k] %= MathUtils.PI_D * 2.0;
					double phase = sumPhase[k];

					fftSamples[(k << 1) + 0] = (float)(magn * MathUtils.cos(phase));
					fftSamples[(k << 1) + 1] = (float)(magn * MathUtils.sin(phase));
					
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
