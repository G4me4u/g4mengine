package com.g4mesoft.graphics3d;

import java.awt.image.BufferedImage;

import com.g4mesoft.math.Vec2f;

public class Texture3D {

	private int[] pixels;

	private int width;
	private int height;
	
	public Texture3D(int[] pixels, int width, int height) {
		this.pixels = pixels;
	
		this.width = width;
		this.height = height;
	}
	
	public Texture3D(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
	}
	

	public int samplePixel(Vec2f uv) {
		return samplePixel(uv.x, uv.y);
	}
	
	public int samplePixel(float u, float v) {
		// Note that the y-coordinate is inverted.
		// To make the origin located at (0, height).
		return samplePixel((int)(u * width), (int)(-v * height));
	}

	public int samplePixel(int x, int y) {
		x %= width;
		y %= height;
		
		if (x < 0)
			x += width;
		if (y < 0)
			y += height;
		
		return pixels[x + y * width];
	}
}
