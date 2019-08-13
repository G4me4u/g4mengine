package com.g4mesoft.graphic.filter;

import com.g4mesoft.math.MathUtils;

/**
 * A simple contrast filter that can be used to change the overall contrast of a
 * view or a raw pixel-array. The contrast is defined as a floating point number
 * and is used to find the new color of every pixel. One channel, red, in the
 * new color is calculated using the following formula:
 * <pre>
 *     // Extract channel from pixel
 *     int r = (pixel >> 16) & 0xFF;
 *     r = clamp((r - 0x7F) * c + 0x7F, 0x00, 0xFF);
 * </pre>
 * Where pixel is the raw pixel in the pixel-array, and c is the contrast as
 * a floating point number.<br>
 * Using the above formula, a contrast of 1.0 will result in the same pixel.
 * If one wishes to invert the pixel colors, they can use a contrast of -1.0.
 * This would be equivalent to the simple formula
 * {@code r = 0xFF - r}.
 * 
 * @author Christian
 * 
 * @see com.g4mesoft.graphic.PixelRenderer2D
 * @see #ContrastPixelFilter(float)
 * @see #setContrast(float)
 */
public class ContrastPixelFilter implements IPixelFilter {

	/**
	 * The contrast of this filter
	 */
	private float contrast;
	
	/**
	 * Constructs a new contrast filter with an initial contrast of {@code 1.0}
	 * to change the contrast, one would use the {@link #setContrast(float)} 
	 * setter-function or the constructor {@link #ContrastPixelFilter(float)}
	 * 
	 * @see #ContrastPixelFilter(float)
	 * @see #setContrast(float)
	 */
	public ContrastPixelFilter() {
		this(1.0f);
	}
	
	/**
	 * Constructs a new contrast pixel filter using the given contrast. Changing
	 * the contrast after construction can be done using the setter-function
	 * {@link #setContrast(float)}. 
	 * 
	 * @param contrast - The initial contrast of this filter
	 * 
	 * @see #setContrast(float)
	 */
	public ContrastPixelFilter(float contrast) {
		this.contrast = contrast;
	}

	@Override
	public void filterPixels(int[] pixels, int offset, int width, int height, int stride) {
		for (int y = 0; y < height; y++) {
			int i = y * stride + offset;
			for (int x = 0; x < width; x++) {
				// Every pixel will go through 4 stages:
				//  1. The extraction stage
				//  2. The contrast stage
				//  3. The clamping stage
				//  4. The storing stage
				
				int pixel = pixels[i];
				int r = (pixel >>> 16) & 0xFF;
				int g = (pixel >>>  8) & 0xFF;
				int b = (pixel >>>  0) & 0xFF;
				
				r = (int)((r - 0x7F) * contrast) + 0x7F;
				g = (int)((g - 0x7F) * contrast) + 0x7F;
				b = (int)((b - 0x7F) * contrast) + 0x7F;
				
				r = MathUtils.clamp(r, 0x00, 0xFF);
				g = MathUtils.clamp(g, 0x00, 0xFF);
				b = MathUtils.clamp(b, 0x00, 0xFF);

				// If the pixel-array contained alpha
				// we should keep it's value
				pixels[i] = (pixel & 0xFF000000) | (r << 16) | (g << 8) | b;
				
				i++;
			}
		}
	}
	
	/**
	 * Sets the contrast for this filter. The contrast is used to calculate the
	 * new pixel color during filtering. 
	 * 
	 * @param contrast - the new contrast of this filter.
	 * 
	 * @see #getContrast()
	 */
	public void setContrast(float contrast) {
		this.contrast = contrast;
	}
	
	/**
	 * @return The contrast of this filter
	 * 
	 * @see #setContrast(float)
	 */
	public float getContrast() {
		return contrast;
	}

}
