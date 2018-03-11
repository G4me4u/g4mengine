package com.g4mesoft.sound.analysis;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;

import com.g4mesoft.Timer;
import com.g4mesoft.sound.SoundManager;
import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.util.MemoryUtil;

public class FastFourierTransform {

	private final int n;
	private final float[] buffer;
	
	public FastFourierTransform(int n) {
		this.n = n;
		
		buffer = new float[n << 1];
	}
	
	public void transformToFFT(float[] data) {
		for (int i = n; i-- > 0; ) {
			buffer[(i << 1) + 0] = data[i];
			buffer[(i << 1) + 1] = 0.0f;
		}
		transform(buffer, -1.0f);
		for (int i = n; i-- > 0; )
			data[i] = buffer[(i << 1) + 0];
	}

	public void transformToSamples(float[] data) {
		for (int i = n >> 1; i-- > 0; ) {
			buffer[(i << 1) + 0] = data[i];
			buffer[(i << 1) + 1] = 0.0f;
		}
		for (int i = n << 1; i-- > n; )
			buffer[i] = 0.0f;
		transform(buffer, 1.0f);
		for (int i = n; i-- > 0; )
			data[i] = buffer[(i << 1) + 0];
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
	
	public static void main(String[] args) throws IOException, AudioParsingException {
		int id = SoundManager.getInstance().loadSound(FastFourierTransform.class.getResourceAsStream("/assets/test.ifiwereaboytest.mp2"));
		if (id == -1) {
			System.out.println("Unable to load sound");
			return;
		}
		AudioFile audio = SoundManager.getInstance().getAudioFile(id);
		
		float vol = 0.5f;
		
		int n = 256;
		int nn = n >> 1;

		byte[] buffer;
				
		// Double data because shorts->bytes
		if (audio.getFormat().getChannels() == 1) {
			buffer = new byte[n << 1];
		} else {
			// double data again because 2 channels
			buffer = new byte[n << 2];
		}
		int br = 0;
		
		int si = audio.getFormat().getChannels() << 1;
		
		float[] fft = new float[n * 2];
		float[] oldfft = new float[n * 2];
		float weight = 0.1f;
		
		Canvas canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(300, 300));
		
		JFrame frame = new JFrame("Preview of FFT");
		frame.add(canvas);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		SoundManager.getInstance().playSound(id).setVolume(vol);
		
		float tps = audio.getFormat().getSampleRate() / n;
		Timer timer = new Timer(tps, false);
		timer.initTimer();
		while (true) {
			timer.update();
			if (timer.getMissingTicks() <= 0)
				continue;

			int bytesRead = audio.getData(buffer, br, 0, buffer.length);
			if (bytesRead < n) {
				try {
					Thread.sleep(500L);
				} catch(InterruptedException e) {
				}
				br = 0;
				SoundManager.getInstance().playSound(id).setVolume(vol);
				timer.initTimer();
				continue;
			}

			br += bytesRead;

			for (int i = 0; i < n; i++) {
				float window = 0.5f * (1.0f - (float)Math.cos((2.0 * Math.PI * i) / (n - 1)));
				fft[i * 2] = window * MemoryUtil.littleEndianToShort(buffer, i * si) / Short.MAX_VALUE;
				fft[i * 2 + 1] = 0.0f;
			}
			
			transform(fft, -1.0f);
			for (int i = 0; i < n; i++) {
				float re = fft[i * 2];
				float im = fft[i * 2 + 1];
				fft[i] = (float)Math.sqrt(re * re + im * im) * weight + oldfft[i] * (1.0f - weight);
			}
			
			oldfft = Arrays.copyOf(fft, fft.length);
			
			int w = canvas.getWidth();
			int h = canvas.getHeight();
			
			BufferStrategy bs = canvas.getBufferStrategy();
			
			if (bs == null) {
				canvas.createBufferStrategy(3);
				bs = canvas.getBufferStrategy();
			}
			
			Graphics g = bs.getDrawGraphics();
			
			float max = 10.0f;
			float tw = (float)w / nn;
			int xo = 0;
			float r = 0.0f;

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, w, h);
			
			for (int i = 0; i < nn; i++) {
				int ww = (int)tw;
				if (r >= 1.0f) {
					r -= 1.0f;
					ww++;
				}
				r += tw % 1.0f;
	
				int hh = (int)(fft[i] / max * h);
				g.setColor(new Color(Color.HSBtoRGB((float)i / nn, 1.0f, 1.0f)));
				g.fillRect(xo, h - hh, ww, hh);
				
				xo += ww;
			}
		
			g.dispose();
			bs.show();
			
			timer.tickPassed();
		}
	}
}
