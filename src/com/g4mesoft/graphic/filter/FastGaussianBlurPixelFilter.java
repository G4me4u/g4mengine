package com.g4mesoft.graphic.filter;

import com.g4mesoft.math.MathUtils;

public class FastGaussianBlurPixelFilter implements IPixelFilter {

	private static final int DEFAULT_NUM_BOXES = 3;
	
	private final int[] boxSizes;
	
	private final int[] bufferR;
	private final int[] bufferG;
	private final int[] bufferB;
	
	public FastGaussianBlurPixelFilter(float radius) {
		boxSizes = calculateSizesForGaussian(radius, DEFAULT_NUM_BOXES);
	
		int bufferLength = MathUtils.max(boxSizes);
		bufferR = new int[bufferLength];
		bufferG = new int[bufferLength];
		bufferB = new int[bufferLength];
	}
	
	private static int[] calculateSizesForGaussian(float radius, int n) {
		float ss = 12.0f * radius * radius;
		float wIdeal = MathUtils.sqrt(ss / n + 1);
		
		int wl = (int)wIdeal;
		if ((wl & 0x01) == 0) wl--;
		int wu = wl + 2;
		
		float mIdeal = (ss - n * wl * wl - 4 * n * wl - 3 * n) / (-4 * wl - 4);
		int m = MathUtils.round(mIdeal);
		
		int[] sizes = new int[n];
		for (int i = 0; i < n; i++)
			sizes[i] = i < m ? wl : wu;

		return sizes;
	}
	
	@Override
	public void filterPixels(int[] pixels, int offset, int width, int height, int stride) {
		for (int i = 0; i < DEFAULT_NUM_BOXES; i++)
			boxBlur(pixels, offset, width, height, stride, boxSizes[i] >>> 1);
	}
	
	private void boxBlur(int[] pixels, int offset, int width, int height, int stride, int rad) {
		horizontalBoxBlur(pixels, offset, width, height, stride, rad);
		verticalBoxBlur(pixels, offset, width, height, stride, rad);
	}
	
	private void horizontalBoxBlur(int[] pixels, int offset, int width, int height, int stride, int rad) {
		int bufferLength = rad + rad + 1;

		float c = 1.0f / bufferLength;
		
		int rgb;
		
		int index = offset;
		for (int y = 0; y < height; y++) {
			int rightIndex = index + rad;
			
			rgb = pixels[index];
			int fr = (rgb >>> 16) & 0xFF;
			int fg = (rgb >>>  8) & 0xFF;
			int fb = (rgb >>>  0) & 0xFF;

			rgb = pixels[index + width - 1];
			int lr = (rgb >>> 16) & 0xFF;
			int lg = (rgb >>>  8) & 0xFF;
			int lb = (rgb >>>  0) & 0xFF;
			
			int ar = (rad + 2) * fr;
			int ag = (rad + 2) * fg;
			int ab = (rad + 2) * fb;
			
			int bufferPos = 0;
			bufferR[bufferPos] = fr;
			bufferG[bufferPos] = fg;
			bufferB[bufferPos] = fb;
			bufferPos++;
			
			int x;
			for (x = 1; x < rad && x < width; x++) {
				rgb = pixels[index + x];
				
				int r = (rgb >>> 16) & 0xFF;
				int g = (rgb >>>  8) & 0xFF;
				int b = (rgb >>>  0) & 0xFF;
				
				ar += r;
				ag += g;
				ab += b;
			
				bufferR[bufferPos] = r;
				bufferG[bufferPos] = g;
				bufferB[bufferPos] = b;
				bufferPos++;
			}
			
			// Perform blur on first part of row 
			// (where some of the box is out of 
			// bounds to the left of image).
			for (x = 0; x <= rad && x + rad < width; x++) {
				rgb = pixels[rightIndex++];
				
				int r = (rgb >>> 16) & 0xFF;
				int g = (rgb >>>  8) & 0xFF;
				int b = (rgb >>>  0) & 0xFF;
				
				bufferR[bufferPos] = r;
				bufferG[bufferPos] = g;
				bufferB[bufferPos] = b;
				bufferPos++;
				
				ar += r - fr;
				ag += g - fg;
				ab += b - fb;

				r = (int)(ar * c);
				g = (int)(ag * c);
				b = (int)(ab * c);
				
				pixels[index++] = (r << 16) | (g << 8) | b;
			}
			
			for (int bound = width - rad; x < bound; x++) {
				rgb = pixels[rightIndex++];
				
				int r = (rgb >>> 16) & 0xFF;
				int g = (rgb >>>  8) & 0xFF;
				int b = (rgb >>>  0) & 0xFF;

				if (bufferPos >= bufferLength)
					bufferPos = 0;

				ar += r - bufferR[bufferPos];
				ag += g - bufferG[bufferPos];
				ab += b - bufferB[bufferPos];

				bufferR[bufferPos] = r;
				bufferG[bufferPos] = g;
				bufferB[bufferPos] = b;
				bufferPos++;
				
				r = (int)(ar * c);
				g = (int)(ag * c);
				b = (int)(ab * c);
				
				pixels[index++] = (r << 16) | (g << 8) | b;
			}
			
			for ( ; x < width; x++) {
				if (bufferPos >= bufferLength)
					bufferPos = 0;
				
				ar += lr - bufferR[bufferPos];
				ag += lg - bufferG[bufferPos];
				ab += lb - bufferB[bufferPos];

				// Shift buffer
				bufferPos++;
				
				int r = (int)(ar * c);
				int g = (int)(ag * c);
				int b = (int)(ab * c);
				
				pixels[index++] = (r << 16) | (g << 8) | b;
			}
			
			index += stride - width;
		}
	}

	private void verticalBoxBlur(int[] pixels, int offset, int width, int height, int stride, int rad) {
		int bufferLength = rad + rad + 1;
		
		float c = 1.0f / bufferLength;
		
		int rgb;
		
		for (int x = 0; x < width; x++) {
			int index = offset + x;
			
			int bottomIndex = index + rad * stride;
			
			rgb = pixels[index];
			int fr = (rgb >>> 16) & 0xFF;
			int fg = (rgb >>>  8) & 0xFF;
			int fb = (rgb >>>  0) & 0xFF;

			rgb = pixels[index + (height - 1) * stride];
			int lr = (rgb >>> 16) & 0xFF;
			int lg = (rgb >>>  8) & 0xFF;
			int lb = (rgb >>>  0) & 0xFF;
			
			int ar = (rad + 2) * fr;
			int ag = (rad + 2) * fg;
			int ab = (rad + 2) * fb;
			
			int bufferPos = 0;
			bufferR[bufferPos] = fr;
			bufferG[bufferPos] = fg;
			bufferB[bufferPos] = fb;
			bufferPos++;
			
			int y;
			for (y = 1; y < rad && y < height; y++) {
				rgb = pixels[index + y * stride];
				
				int r = (rgb >>> 16) & 0xFF;
				int g = (rgb >>>  8) & 0xFF;
				int b = (rgb >>>  0) & 0xFF;
				
				ar += r;
				ag += g;
				ab += b;
				
				bufferR[bufferPos] = r;
				bufferG[bufferPos] = g;
				bufferB[bufferPos] = b;
				bufferPos++;
			}
			
			// Perform blur on first part of column 
			// (where some of the box is out of 
			// bounds to the top of image).
			for (y = 0; y <= rad && y + rad < height; y++) {
				rgb = pixels[bottomIndex];
				
				int r = (rgb >>> 16) & 0xFF;
				int g = (rgb >>>  8) & 0xFF;
				int b = (rgb >>>  0) & 0xFF;
				
				bufferR[bufferPos] = r;
				bufferG[bufferPos] = g;
				bufferB[bufferPos] = b;
				bufferPos++;
				
				ar += r - fr;
				ag += g - fg;
				ab += b - fb;

				r = (int)(ar * c);
				g = (int)(ag * c);
				b = (int)(ab * c);
				
				pixels[index] = (r << 16) | (g << 8) | b;

				index += stride;
				bottomIndex += stride;
			}
			
			// Perform center blur (where entire box is
			// in the bounds of the image).
			for (int bound = height - rad; y < bound; y++) {
				rgb = pixels[bottomIndex];
				
				int r = (rgb >>> 16) & 0xFF;
				int g = (rgb >>>  8) & 0xFF;
				int b = (rgb >>>  0) & 0xFF;

				if (bufferPos >= bufferLength)
					bufferPos = 0;

				ar += r - bufferR[bufferPos];
				ag += g - bufferG[bufferPos];
				ab += b - bufferB[bufferPos];

				bufferR[bufferPos] = r;
				bufferG[bufferPos] = g;
				bufferB[bufferPos] = b;
				bufferPos++;
				
				r = (int)(ar * c);
				g = (int)(ag * c);
				b = (int)(ab * c);
				
				pixels[index] = (r << 16) | (g << 8) | b;
				
				index += stride;
				bottomIndex += stride;
			}
			
			// Perform bottom blur, where the box is 
			// outside of the bottom part of the image.
			for ( ; y < height; y++) {
				if (bufferPos >= bufferLength)
					bufferPos = 0;
				
				ar += lr - bufferR[bufferPos];
				ag += lg - bufferG[bufferPos];
				ab += lb - bufferB[bufferPos];

				// Shift buffer
				bufferPos++;
				
				int r = (int)(ar * c);
				int g = (int)(ag * c);
				int b = (int)(ab * c);
				
				pixels[index] = (r << 16) | (g << 8) | b;
				
				index += stride;
			}
		}
	}
}
