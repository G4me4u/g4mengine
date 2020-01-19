package com.g4mesoft.graphic.filter;

public class BoxBlurPixelFilter implements IPixelFilter {

	public static final int HORIZONTAL_BLUR = 1;
	public static final int VERTICAL_BLUR = 2;
	public static final int FULL_BLUR = HORIZONTAL_BLUR | VERTICAL_BLUR;
	
	private final int radius;
	private final int flags;
	
	private final int[] bufferR;
	private final int[] bufferG;
	private final int[] bufferB;

	public BoxBlurPixelFilter(int radius) {
		this(radius, FULL_BLUR);
	}
	
	public BoxBlurPixelFilter(int radius, int flags) {
		if (radius < 0)
			throw new IllegalArgumentException("radius < 0");
		if ((flags & (~FULL_BLUR)) != 0)
			throw new IllegalArgumentException("Invalid blur flags");
		
		this.radius = radius;
		this.flags = flags;
		
		bufferR = new int[radius * 2 + 1];
		bufferG = new int[radius * 2 + 1];
		bufferB = new int[radius * 2 + 1];
	}
	
	@Override
	public void filterPixels(int[] pixels, int offset, int width, int height, int stride) {
		if ((flags & HORIZONTAL_BLUR) != 0)
			horizontalBoxBlur(pixels, offset, width, height, stride);
		if ((flags & VERTICAL_BLUR) != 0)
			verticalBoxBlur(pixels, offset, width, height, stride);
	}
	
	protected void horizontalBoxBlur(int[] pixels, int offset, int width, int height, int stride) {
		if (radius <= 0)
			return;
		
		int bufferLength = radius + radius + 1;

		float c = 1.0f / bufferLength;
		
		int rgb;
		
		int index = offset;
		for (int y = 0; y < height; y++) {
			int rightIndex = index + radius;
			
			rgb = pixels[index];
			int fr = (rgb >>> 16) & 0xFF;
			int fg = (rgb >>>  8) & 0xFF;
			int fb = (rgb >>>  0) & 0xFF;

			rgb = pixels[index + width - 1];
			int lr = (rgb >>> 16) & 0xFF;
			int lg = (rgb >>>  8) & 0xFF;
			int lb = (rgb >>>  0) & 0xFF;
			
			int ar = (radius + 2) * fr;
			int ag = (radius + 2) * fg;
			int ab = (radius + 2) * fb;
			
			int bufferPos = 0;
			bufferR[bufferPos] = fr;
			bufferG[bufferPos] = fg;
			bufferB[bufferPos] = fb;
			bufferPos++;
			
			int x;
			for (x = 1; x < radius && x < width; x++) {
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
			for (x = 0; x <= radius && x + radius < width; x++) {
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
			
			for (int bound = width - radius; x < bound; x++) {
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

	protected void verticalBoxBlur(int[] pixels, int offset, int width, int height, int stride) {
		if (radius <= 0)
			return;
		
		int bufferLength = radius + radius + 1;
		
		float c = 1.0f / bufferLength;
		
		int rgb;
		
		for (int x = 0; x < width; x++) {
			int index = offset + x;
			
			int bottomIndex = index + radius * stride;
			
			rgb = pixels[index];
			int fr = (rgb >>> 16) & 0xFF;
			int fg = (rgb >>>  8) & 0xFF;
			int fb = (rgb >>>  0) & 0xFF;

			rgb = pixels[index + (height - 1) * stride];
			int lr = (rgb >>> 16) & 0xFF;
			int lg = (rgb >>>  8) & 0xFF;
			int lb = (rgb >>>  0) & 0xFF;
			
			int ar = (radius + 2) * fr;
			int ag = (radius + 2) * fg;
			int ab = (radius + 2) * fb;
			
			int bufferPos = 0;
			bufferR[bufferPos] = fr;
			bufferG[bufferPos] = fg;
			bufferB[bufferPos] = fb;
			bufferPos++;
			
			int y;
			for (y = 1; y < radius && y < height; y++) {
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
			for (y = 0; y <= radius && y + radius < height; y++) {
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
			for (int bound = height - radius; y < bound; y++) {
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

	public static void filterPixels(int radius, int[] pixels, int width, int height) {
		filterPixels(radius, FULL_BLUR, pixels, width, height);
	}

	public static void filterPixels(int radius, int flags, int[] pixels, int width, int height) {
		filterPixels(radius, flags, pixels, 0, width, height, width);
	}

	public static void filterPixels(int radius, int[] pixels, int offset, int width, int height, int stride) {
		filterPixels(radius, FULL_BLUR, pixels, offset, width, height, stride);
	}
	
	public static void filterPixels(int radius, int flags, int[] pixels, int offset, int width, int height, int stride) {
		new BoxBlurPixelFilter(radius, flags).filterPixels(pixels, offset, width, height, stride);
	}
}
