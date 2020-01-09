package com.g4mesoft.graphics3d;

import com.g4mesoft.math.MathUtils;

public class Fragment3D {

	private int value;

	private int denorm(float v) {
		return MathUtils.clamp((int)(v * 255.0f), 0x00, 0xFF);
	}
	
	public void blend(float da, float dr, float dg, float db) {
		blend(denorm(da), denorm(dr), denorm(dg), denorm(db));
	}

	public void blend(int da, int dr, int dg, int db) {
		int soa = (0xFF - da) * getAlpha();
		
		int oa = da + soa / 0xFF;
		if (oa == 0) {
			value = 0;
			return;
		}
		
		int or = (soa * getRed()   / 0xFF + da * dr) / oa;
		int og = (soa * getGreen() / 0xFF + da * dg) / oa;
		int ob = (soa * getBlue()  / 0xFF + da * db) / oa;
		setARGB(oa, or, og, ob);
	}
	
	public void setRGB(float r, float g, float b) {
		setARGB(1.0f, r, g, b);
	}

	public void setARGB(float a, float r, float g, float b) {
		setARGB(denorm(a), denorm(r), denorm(g), denorm(b));
	}
	
	public void setRGB(int r, int g, int b) {
		setARGB(0xFF, r, g, b);
	}

	public void setRGB(int rgb) {
		setARGB(0xFF, rgb);
	}

	public void setARGB(int a, int r, int g, int b) {
		value = (a << 24) | (r << 16) | (g << 8) | b;
	}

	public void setARGB(int a, int rgb) {
		value = (a << 24) | rgb;
	}

	public void setARGB(int argb) {
		value = argb;
	}
	
	public int getRGB() {
		return value & 0x00FFFFFF;
	}

	public int getARGB() {
		return value;
	}
	
	public int getAlpha() {
		return (value >>> 24) & 0xFF;
	}

	public int getRed() {
		return (value >>> 16) & 0xFF;
	}

	public int getGreen() {
		return (value >>> 8) & 0xFF;
	}

	public int getBlue() {
		return (value >>> 0) & 0xFF;
	}
}
