package com.g4mesoft.graphic.filter;

import com.g4mesoft.math.MathUtils;

public class FastGaussianBlurPixelFilter implements IPixelFilter {

	public static final int HORIZONTAL_BLUR = BoxBlurPixelFilter.HORIZONTAL_BLUR;
	public static final int VERTICAL_BLUR = BoxBlurPixelFilter.VERTICAL_BLUR;
	public static final int FULL_BLUR = BoxBlurPixelFilter.FULL_BLUR;

	private static final int DEFAULT_NUM_BOXES = 3;
	
	private final BoxBlurPixelFilter[] boxBlurs;
	
	public FastGaussianBlurPixelFilter(float radius) {
		this(radius, FULL_BLUR);
	}

	public FastGaussianBlurPixelFilter(float radius, int flags) {
		boxBlurs = new BoxBlurPixelFilter[DEFAULT_NUM_BOXES];
		
		int[] boxSizes = calculateSizesForGaussian(radius, DEFAULT_NUM_BOXES);
		for (int i = 0; i < DEFAULT_NUM_BOXES; i++)
			boxBlurs[i] = new BoxBlurPixelFilter(boxSizes[i] / 2, flags);
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
			boxBlurs[i].filterPixels(pixels, offset, width, height, stride);
	}
}
