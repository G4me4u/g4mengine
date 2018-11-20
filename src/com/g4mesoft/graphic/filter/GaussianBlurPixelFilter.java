package com.g4mesoft.graphic.filter;

import com.g4mesoft.math.MathUtils;

public class GaussianBlurPixelFilter implements IPixelFilter {

	private static final float DEFAULT_SIGMA = 1.0f;
	
	private final int kernelSize;
	private final float[] kernel;
	
	private final float[] tmpPixelsR;
	private final float[] tmpPixelsG;
	private final float[] tmpPixelsB;
	
	public GaussianBlurPixelFilter(int kernelSize) {
		this(kernelSize, DEFAULT_SIGMA);
	}
	
	public GaussianBlurPixelFilter(int kernelSize, float sigma) {
		// The kernel size must be an uneven
		// integer greater than or equal to 3.
		if (kernelSize < 1 || (kernelSize & 0x01) == 0)
			throw new IllegalArgumentException("Invalid kernel size!");
		
		// Sigma must be greater than zero
		if (sigma <= MathUtils.EPSILON)
			throw new IllegalArgumentException("Invalid sigma (standard diviation)!");
			
		this.kernelSize = kernelSize;
		kernel = calculateKernel(kernelSize, sigma);

		tmpPixelsR = new float[kernelSize];
		tmpPixelsG = new float[kernelSize];
		tmpPixelsB = new float[kernelSize];
	}
	
	private static float[] calculateKernel(int kernelSize, float sigma) {
		float[] kernel = new float[kernelSize];
		
		// Calculate kernel values
		float a = 1.0f / ((float)Math.sqrt(2.0f * Math.PI) * sigma);
		float b = -0.5f / (sigma * sigma);
		
		// The gaussian blur kernel function is
		// defined as c = a * exp(b * x * x). Where
		// x is the distance from the center of
		// the kernel.
		int halfKernelSize = kernelSize / 2;

		float sum = 0.0f;
		for (int i = 0; i < halfKernelSize; i++) {
			float x = i - halfKernelSize;
			float c = a * (float)Math.exp(b * x * x);
			
			// Kernel is symmetrical.
			kernel[i] = kernel[kernelSize - 1 - i] = c;
			sum += 2.0f * c;
		}
		
		// Center value in kernel has
		// x = 0. Therefore the value
		// is simply c = a * exp(0) = a;
		kernel[halfKernelSize] = a;
		sum += a;
		
		// Normalize kernel
		for (int i = 0; i < kernelSize; i++)
			kernel[i] /= sum;
		
		return kernel;
	}
	
	@Override
	public void filterPixels(int[] pixels, int width, int height) {
		horizontalBlur(pixels, width, height);
		verticalBlur(pixels, width, height);
	}
	
	public void horizontalBlur(int[] pixels, int width, int height) {
		float r, g, b;
		
		// The number of pixels to the 
		// right of the center pixel.
		int halfKernelSize = kernelSize >>> 1;
		int kernelSizeMO = kernelSize - 1;
		
		int pixel;
		int index = 0;
		for (int y = 0; y < height; y++) {
			// Setup temp pixels for next row
			// with leading zeroes.
			for (int i = 0; i < halfKernelSize; i++) {
				tmpPixelsR[kernelSizeMO] = 0;
				tmpPixelsG[kernelSizeMO] = 0;
				tmpPixelsB[kernelSizeMO] = 0;
			}

			// Gather temp pixels
			for (int i = halfKernelSize; i < kernelSize; i++) {
				pixel = pixels[index + i - halfKernelSize];
				
				tmpPixelsR[i] = (pixel >>> 16) & 0xFF;
				tmpPixelsG[i] = (pixel >>>  8) & 0xFF;
				tmpPixelsB[i] = (pixel       ) & 0xFF;
			}
			
			for (int x = 0; x < width; x++) {
				float c = kernel[0];
				
				r = c * tmpPixelsR[0];
				g = c * tmpPixelsG[0];
				b = c * tmpPixelsB[0];
				
				// Calculate new pixel values
				for (int i = 1; i < kernelSize; i++) {
					c = kernel[i];
					
					// Shift temp pixels to
					// the left.
					r += c * (tmpPixelsR[i - 1] = tmpPixelsR[i]);
					g += c * (tmpPixelsG[i - 1] = tmpPixelsG[i]);
					b += c * (tmpPixelsB[i - 1] = tmpPixelsB[i]);
				}
				
				// Gather next pixel for temp
				// pixel array.
				if (x + halfKernelSize < width) {
					pixel = pixels[index + halfKernelSize];
					
					tmpPixelsR[kernelSizeMO] = (pixel >>> 16) & 0xFF;
					tmpPixelsG[kernelSizeMO] = (pixel >>>  8) & 0xFF;
					tmpPixelsB[kernelSizeMO] = (pixel       ) & 0xFF;
				} else {
					tmpPixelsR[kernelSizeMO] = 0;
					tmpPixelsG[kernelSizeMO] = 0;
					tmpPixelsB[kernelSizeMO] = 0;
				}
				
				// We're truncating towards
				// zero, so we'll never get
				// rgb values above 255.
				pixels[index++] = ((int)r << 16) | ((int)g << 8) | (int)b;
			}
		}
	}
	
	public void verticalBlur(int[] pixels, int width, int height) {
		float r, g, b;
		
		// The number of pixels above the 
		// center pixel.
		int halfKernelSize = kernelSize >>> 1;
		int kernelSizeMO = kernelSize - 1;
		
		int pixel;
		for (int x = 0; x < width; x++) {
			int index = x;

			// Setup temp pixels for next column
			// with leading zeroes.
			int i;
			for (i = 0; i < halfKernelSize; i++) {
				tmpPixelsR[kernelSizeMO] = 0;
				tmpPixelsG[kernelSizeMO] = 0;
				tmpPixelsB[kernelSizeMO] = 0;
			}

			// Gather temp pixels
			for ( ; i < kernelSize; i++) {
				pixel = pixels[index + (i - halfKernelSize) * width];
				
				tmpPixelsR[i] = (pixel >>> 16) & 0xFF;
				tmpPixelsG[i] = (pixel >>>  8) & 0xFF;
				tmpPixelsB[i] = (pixel       ) & 0xFF;
			}
			
			for (int y = 0; y < height; y++) {
				float c = kernel[0];
				
				r = c * tmpPixelsR[0];
				g = c * tmpPixelsG[0];
				b = c * tmpPixelsB[0];
				
				// Calculate new pixel values
				for (i = 1; i < kernelSize; i++) {
					c = kernel[i];
					
					// Shift temp pixels to
					// the left.
					r += c * (tmpPixelsR[i - 1] = tmpPixelsR[i]);
					g += c * (tmpPixelsG[i - 1] = tmpPixelsG[i]);
					b += c * (tmpPixelsB[i - 1] = tmpPixelsB[i]);
				}
				
				// Gather next pixel for temp
				// pixel array.
				if (y + halfKernelSize < height) {
					pixel = pixels[index + halfKernelSize * width];
					
					tmpPixelsR[kernelSizeMO] = (pixel >>> 16) & 0xFF;
					tmpPixelsG[kernelSizeMO] = (pixel >>>  8) & 0xFF;
					tmpPixelsB[kernelSizeMO] = (pixel       ) & 0xFF;
				} else {
					tmpPixelsR[kernelSizeMO] = 0;
					tmpPixelsG[kernelSizeMO] = 0;
					tmpPixelsB[kernelSizeMO] = 0;
				}
				
				// We're truncating towards
				// zero, so we'll never get
				// rgb values above 255.
				pixels[index] = ((int)r << 16) | ((int)g << 8) | (int)b;
				index += width;
			}
		}
	}
}