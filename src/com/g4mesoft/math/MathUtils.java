package com.g4mesoft.math;

public final class MathUtils {

	public static final float EPSILON = 0.1f;
	public static final double EPSILON_D = 0.1;
	
	private MathUtils() {
	}
	
	public static float min(float a, float b) {
		return a < b ? a : b;
	}
	
	public static float max(float a, float b) {
		return a > b ? a : b;
	}
	
	public static float clamp(float v, float mn, float mx) {
		return v > mx ? mx : (v < mn ? mn : v);
	}
	
	public static double min(double a, double b) {
		return a < b ? a : b;
	}

	public static double max(double a, double b) {
		return a > b ? a : b;
	}

	public static double clamp(double v, double mn, double mx) {
		return v > mx ? mx : (v < mn ? mn : v);
	}
	
	public static int min(int a, int b) {
		return a < b ? a : b;
	}
	
	public static int max(int a, int b) {
		return a > b ? a : b;
	}
	
	public int clamp(int v, int mn, int mx) {
		return v > mx ? mx : (v < mn ? mn : v);
	}
	
	public static long min(long a, long b) {
		return a < b ? a : b;
	}
	
	public static long max(long a, long b) {
		return a > b ? a : b;
	}
	
	public static long clamp(long v, long mn, long mx) {
		return v > mx ? mx : (v < mn ? mn : v);
	}
}
