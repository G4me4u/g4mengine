package com.g4mesoft.graphic.filter;

import com.g4mesoft.math.MathUtils;

public class MultiplyPixelFilter implements IPixelFilter {

	private final float mulRed;
	private final float mulGreen;
	private final float mulBlue;
	
	public MultiplyPixelFilter(float multiplier) {
		this(multiplier, multiplier, multiplier);
	}
	
	public MultiplyPixelFilter(float mulRed, float mulGreen, float mulBlue) {
		this.mulRed = mulRed;
		this.mulGreen = mulGreen;
		this.mulBlue = mulBlue;
	}
	
	@Override
	public void filterPixels(int[] pixels, int offset, int width, int height, int stride) {
		for (int y = 0; y < height; y++) {
			int i = y * stride + offset;
			for (int x = 0; x < width; x++) {
				int pixel = pixels[i];
				int r = (pixel >>> 16) & 0xFF;
				int g = (pixel >>>  8) & 0xFF;
				int b = (pixel >>>  0) & 0xFF;
				
				r = MathUtils.clamp((int)(r * mulRed),   0x00, 0xFF);
				g = MathUtils.clamp((int)(g * mulGreen), 0x00, 0xFF);
				b = MathUtils.clamp((int)(b * mulBlue),  0x00, 0xFF);

				pixels[i] = (pixel & 0xFF000000) | (r << 16) | (g << 8) | b;
				
				i++;
			}
		}
	}
	
	public static void filterPixels(float multiplier, int[] pixels, int width, int height) {
		filterPixels(multiplier, multiplier, multiplier, pixels, width, height);
	}

	public static void filterPixels(float mulRed, float mulGreen, float mulBlue, 
			int[] pixels, int width, int height) {
		
		filterPixels(mulRed, mulGreen, mulBlue, pixels, 0, width, height, width);
	}

	public static void filterPixels(float multiplier, int[] pixels, int offset, 
			int width, int height, int stride) {
		
		filterPixels(multiplier, multiplier, multiplier, pixels, offset, width, height, stride);
	}

	public static void filterPixels(float mulRed, float mulGreen, float mulBlue, 
			int[] pixels, int offset, int width, int height, int stride) {
	
		new MultiplyPixelFilter(mulRed, mulGreen, mulBlue).filterPixels(pixels, offset, width, height, stride);
	}
}
