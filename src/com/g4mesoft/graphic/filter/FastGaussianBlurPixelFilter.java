package com.g4mesoft.graphic.filter;

public class FastGaussianBlurPixelFilter implements IPixelFilter {

	private static final int DEFAULT_NUM_BOXES = 3;
	
	private final int[] boxSizes;
	
	public FastGaussianBlurPixelFilter(float sigma) {
		boxSizes = calculateSizesForGaussian(sigma, DEFAULT_NUM_BOXES);
	}
	
	private static int[] calculateSizesForGaussian(float sigma, int n) {
		float ss = 12.0f * sigma * sigma;
		float wIdeal = (float)Math.sqrt(ss / n + 1);
		
		int wl = (int)wIdeal;
		if ((wl & 0x01) == 0) wl--;
		int wu = wl + 2;
		
		float mIdeal = (ss - n * wl * wl - 4 * n * wl - 3 * n) / (-4 * wl - 4);
		float m = Math.round(mIdeal);
		
		int[] sizes = new int[n];
		for (int i = 0; i < n; i++)
			sizes[i] = i < m ? wl : wu;

		return sizes;
	}
	
	@Override
	public void filterPixels(int[] pixels, int width, int height) {
		for (int i = 0; i < DEFAULT_NUM_BOXES; i++)
			boxBlur(pixels, width, height, boxSizes[i] >>> 1);
	}
	
	private void boxBlur(int[] pixels, int width, int height, int rad) {
		horizontalBoxBlur(pixels, width, height, rad);
		verticalBoxBlur(pixels, width, height, rad);
	}
	
	private void horizontalBoxBlur(int[] pixels, int width, int height, int rad) {
		float c = 1.0f / (rad + rad + 1);
		
		int rgb;
		
		int index = 0;
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
			
			PixelNode bufferStart = new PixelNode(fr, fg, fb);
			PixelNode bufferEnd = bufferStart;
			PixelNode tmpNode;
			
			int x;
			for (x = 1; x < rad && x < width; x++) {
				rgb = pixels[index + x];
				
				int r = (rgb >>> 16) & 0xFF;
				int g = (rgb >>>  8) & 0xFF;
				int b = (rgb >>>  0) & 0xFF;
				
				ar += r;
				ag += g;
				ab += b;
				
				tmpNode = bufferEnd;
				bufferEnd = new PixelNode(r, g, b);
				tmpNode.next = bufferEnd;
			}
			
			// Perform blur on first part of row 
			// (where some of the box is out of 
			// bounds to the left of image).
			for (x = 0; x <= rad && x + rad < width; x++) {
				rgb = pixels[rightIndex++];
				
				int r = (rgb >>> 16) & 0xFF;
				int g = (rgb >>>  8) & 0xFF;
				int b = (rgb >>>  0) & 0xFF;
				
				tmpNode = bufferEnd;
				bufferEnd = new PixelNode(r, g, b);
				tmpNode.next = bufferEnd;
				
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

				ar += r - bufferStart.r;
				ag += g - bufferStart.g;
				ab += b - bufferStart.b;
				
				bufferStart.r = r;
				bufferStart.g = g;
				bufferStart.b = b;
				
				bufferEnd.next = bufferStart;
				bufferEnd = bufferStart;
				bufferStart = bufferStart.next;
				
				r = (int)(ar * c);
				g = (int)(ag * c);
				b = (int)(ab * c);
				
				pixels[index++] = (r << 16) | (g << 8) | b;
			}
			
			for ( ; x < width; x++) {
				ar += lr - bufferStart.r;
				ag += lg - bufferStart.g;
				ab += lb - bufferStart.b;
				
				// Shift buffer
				bufferStart = bufferStart.next;
				
				int r = (int)(ar * c);
				int g = (int)(ag * c);
				int b = (int)(ab * c);
				
				pixels[index++] = (r << 16) | (g << 8) | b;
			}
		}
	}

	private void verticalBoxBlur(int[] pixels, int width, int height, int rad) {
		float c = 1.0f / (rad + rad + 1);
		
		int rgb;
		
		for (int x = 0; x < width; x++) {
			int index = x;
			
			int bottomIndex = index + rad * width;
			
			rgb = pixels[index];
			int fr = (rgb >>> 16) & 0xFF;
			int fg = (rgb >>>  8) & 0xFF;
			int fb = (rgb >>>  0) & 0xFF;

			rgb = pixels[index + (height - 1) * width];
			int lr = (rgb >>> 16) & 0xFF;
			int lg = (rgb >>>  8) & 0xFF;
			int lb = (rgb >>>  0) & 0xFF;
			
			int ar = (rad + 2) * fr;
			int ag = (rad + 2) * fg;
			int ab = (rad + 2) * fb;
			
			PixelNode bufferStart = new PixelNode(fr, fg, fb);
			PixelNode bufferEnd = bufferStart;
			PixelNode tmpNode;
			
			int y;
			for (y = 1; y < rad && y < height; y++) {
				rgb = pixels[index + y * width];
				
				int r = (rgb >>> 16) & 0xFF;
				int g = (rgb >>>  8) & 0xFF;
				int b = (rgb >>>  0) & 0xFF;
				
				ar += r;
				ag += g;
				ab += b;
				
				tmpNode = bufferEnd;
				bufferEnd = new PixelNode(r, g, b);
				tmpNode.next = bufferEnd;
			}
			
			// Perform blur on first part of column 
			// (where some of the box is out of 
			// bounds to the top of image).
			for (y = 0; y <= rad && y + rad < height; y++) {
				rgb = pixels[bottomIndex];
				
				int r = (rgb >>> 16) & 0xFF;
				int g = (rgb >>>  8) & 0xFF;
				int b = (rgb >>>  0) & 0xFF;
				
				tmpNode = bufferEnd;
				bufferEnd = new PixelNode(r, g, b);
				tmpNode.next = bufferEnd;
				
				ar += r - fr;
				ag += g - fg;
				ab += b - fb;

				r = (int)(ar * c);
				g = (int)(ag * c);
				b = (int)(ab * c);
				
				pixels[index] = (r << 16) | (g << 8) | b;

				index += width;
				bottomIndex += width;
			}
			
			// Perform center blur (where entire box is
			// in the bounds of the image).
			for (int bound = height - rad; y < bound; y++) {
				rgb = pixels[bottomIndex];
				
				int r = (rgb >>> 16) & 0xFF;
				int g = (rgb >>>  8) & 0xFF;
				int b = (rgb >>>  0) & 0xFF;

				ar += r - bufferStart.r;
				ag += g - bufferStart.g;
				ab += b - bufferStart.b;
				
				bufferStart.r = r;
				bufferStart.g = g;
				bufferStart.b = b;
				
				bufferEnd.next = bufferStart;
				bufferEnd = bufferStart;
				bufferStart = bufferStart.next;
				
				r = (int)(ar * c);
				g = (int)(ag * c);
				b = (int)(ab * c);
				
				pixels[index] = (r << 16) | (g << 8) | b;
				
				index += width;
				bottomIndex += width;
			}
			
			// Perform bottom blur, where the box is 
			// outside of the bottom part of the image.
			for ( ; y < height; y++) {
				ar += lr - bufferStart.r;
				ag += lg - bufferStart.g;
				ab += lb - bufferStart.b;
				
				// Shift buffer
				bufferStart = bufferStart.next;
				
				int r = (int)(ar * c);
				int g = (int)(ag * c);
				int b = (int)(ab * c);
				
				pixels[index] = (r << 16) | (g << 8) | b;
				
				index += width;
			}
		}
	}
	
	
	private class PixelNode {
		
		private int r;
		private int g;
		private int b;
		
		private PixelNode next;
		
		public PixelNode(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}
}
