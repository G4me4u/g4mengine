package com.g4mesoft.sound.analysis;

public final class FastFourierTransform {

	private FastFourierTransform() {
	}
	
	public static void transform(float[] data, float sign) {
		int n = data.length;
		
		// No need for FFT
		if (n <= 1) return;
		
		// Length is not power of two!
		if ((n & (n - 1)) != 0) 
			throw new IllegalArgumentException("data.length is not a power of two!");
		
		int i, j, m;
		
		float tmp;
		int nn = n >>> 1;
		for (i = 1, j = 1; i < n; i += 2) {
			if (i > j) {
				tmp         = data[j - 1];
				data[j - 1] = data[i - 1];
				data[i - 1] = tmp;

				tmp     = data[j];
				data[j] = data[i];
				data[i] = tmp;
			}
			
			m = nn;
			while (m >= 2 && j > m) {
				j -= m;
				m >>>= 1;
			}
			j += m;
		}
		
		int mmax = 2;
		while (n > mmax) {
			int istep = mmax << 1;
			double theta = Math.PI / (mmax >>> 1);
			
			float ur = 1.0f;
			float ui = 0.0f;

			float wr = (float)Math.cos(theta);
			float wi = sign * (float)Math.sin(theta);
			
			for (m = 0; m < mmax; m += 2) {
				for (i = m; i < n; i += istep) {
					j = i + mmax;
					float tempr = ur * data[j] - ui * data[j + 1];
					float tempi = ui * data[j] + ur * data[j + 1];
					
					data[j]     = data[i]     - tempr;
					data[j + 1] = data[i + 1] - tempi;
					
					data[i]     += tempr;
					data[i + 1] += tempi;
				}
				
				tmp = ur * wr - ui * wi;
				ui  = ur * wi + ui * wr;
				ur  = tmp;
			}
			
			mmax = istep;
		}
	}
}
