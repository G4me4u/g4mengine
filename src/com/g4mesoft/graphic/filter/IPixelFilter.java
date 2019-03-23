package com.g4mesoft.graphic.filter;

public interface IPixelFilter {

	/**
	 * Performs the filter implementation on the given pixel array within view
	 * bounds. The pixel array should be stored using y-major ordering. In other
	 * words, an index to a given pixel at position (x, y) should be accessible
	 * by the following code snippet:
	 * <pre>
	 *   // Index for y-major
	 *   int indexY = x + y * stride;
	 * </pre>
	 * The view bounds startX and startY is defined by the offset in the pixel 
	 * array. Since the pixels are stored in y-major ordering, the following
	 * would be the offset index of a given startX and startY position:
	 * <pre>
	 *   int offset = startX + startY * stride
	 * </pre>
	 * The width and height are the size of the view in which the filter should
	 * be applied. Any implementation of the IPixelFilter interface should be
	 * sure to follow these requirements. Stride defines the number of pixels in
	 * a single row, i.e. the width of the viewport that the pixels point to.
	 * 
	 * @param pixels - The pixels array
	 * @param offset - The start offset in the pixels array
	 * @param width - The width of the area affected by the filter
	 * @param height - The height of the area affected by the filter
	 * @param stride - The stride of the pixels array
	 * 
	 * @see #filterPixels(int[], int, int)
	 */
	public void filterPixels(int[] pixels, int offset, int width, int height, int stride);
	
	/**
	 * Performs the filter implementation on the given pixel array within view
	 * bounds given by (0, 0, width, height) with {@code stride = width}. The 
	 * pixels array should be ordered using y-major ordering. If the pixels
	 * array does not have {@code stride = width}, then it should be considered
	 * using {@link #filterPixels(int[], int, int, int, int)} instead.
	 * 
	 * @param pixels - The pixels array
	 * @param width - The width of the area affected by the filter
	 * @param height - The height of the area affected by the filter
	 * 
	 * @see #filterPixels(int[], int, int, int, int)
	 */
	default public void filterPixels(int[] pixels, int width, int height) {
		filterPixels(pixels, 0, width, height, width);
	}
}
