package com.g4mesoft.sound.format.mpeg;

import java.util.Arrays;

public class MPEGSynthesisSubbandFilter {

	private float[][] v;
	private float[] u;
	
	public MPEGSynthesisSubbandFilter() {
		v = new float[2][1024];
		u = new float[512];
	
		Arrays.fill(v[0], 0.0f);
		Arrays.fill(v[1], 0.0f);
	}

	/**
	 * Synthesizes the samples given as a parameter.
	 * 
	 * NOTE: The samples have to be formatted, so
	 * you can get an index of a sample by doing
	 * the following calculation:
	 * 
	 * <pre>
	 *   si = ch + (sb + s * 32) * 2
	 * </pre>
	 * 
	 * @param samples
	 * @param nch
	 * @param ns
	 */
	public void synthesizeSamples(float[] samples, int nch, int ns) {
		int s;

		int i, j, k;
		int tp, wp;
		int spc, sp;
		
		for (int ch = 0; ch < nch; ch++) {
			float[] v = this.v[ch];
			
			for (s = 0; s < ns; s++) {
				sp = ch + (s * 32 << 1);
				
				// Shifting
				for (i = 1023; i >= 64; i--)
					v[i] = v[i - 64];
				
				// A native, but unfortunately slower way:
				// System.arraycopy(v, 0, v, 64, 1024 - 64);
				
				// Matrixing
				tp = 0;
				for (i = 0; i < 64; i++) {
					v[i] = 0.0f;
					
					spc = sp;
					for (k = 0; k < 32; k++) {
						v[i] += MPEGTables.L12_SYNTH_SUBBAND_FILER_TABLE[tp++] * samples[spc];
						spc += 2;
					}
				}
				
				// Build a 512 values vector U
				// And reset samples before adding W
				spc = sp;
				for (j = 31; j >= 0; j--) {
					samples[spc] = 0.0f;
					spc += 2;

					for (i = 7; i >= 0; i--) {
						u[i * 64 +  0 + j] = v[i * 128 +  0 + j];
						u[i * 64 + 32 + j] = v[i * 128 + 96 + j];
					}
				}
				
				// Calculate 32 samples
				wp = 0;
				for (i = 15; i >= 0; i--) {
					spc = sp;
					for (j = 0; j < 32; j++) {
						// Window and sum
						samples[spc] += MPEGTables.L12_SYNTH_WINDOW_COEFFICIENTS_TABLE[wp] * u[wp++];
						spc += 2;
					}
				}
			}
		}
	}
}
