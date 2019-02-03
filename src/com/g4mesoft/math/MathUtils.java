package com.g4mesoft.math;

public final class MathUtils {

	public static final float EPSILON = 0.001f;
	public static final double EPSILON_D = 0.001;
	
	public static final float PI = 3.1415927f;
	public static final double PI_D = 3.14159265358979323846;
	
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

	public static boolean nearZero(float v) {
		return v < EPSILON && v > -EPSILON;
	}

	public static boolean nearZero(double v) {
		return v < EPSILON_D && v > -EPSILON_D;
	}

	public static float abs(float v) {
		return v < 0.0f ? -v : v;
	}

	public static double abs(double v) {
		return v < 0.0 ? -v : v;
	}
	
	public static int abs(int v) {
		return v < 0 ? -v : v;
	}
	
	public static long abs(long v) {
		return v < 0L ? -v : v;
	}
	
	public static float sqrt(float v) {
		return (float)Math.sqrt(v);
	}

	public static double sqrt(double v) {
		return Math.sqrt(v);
	}

	public static float cbrt(float v) {
		return (float)Math.cbrt(v);
	}
	
	public static double cbrt(double v) {
		return Math.cbrt(v);
	}
	
	public static float cos(float theta) {
		return (float)Math.cos(theta);
	}

	public static double cos(double theta) {
		return Math.cos(theta);
	}
	
	public static float sin(float theta) {
		return (float)Math.sin(theta);
	}
	
	public static double sin(double theta) {
		return Math.sin(theta);
	}
	
	public static float tan(float theta) {
		return (float)Math.tan(theta);
	}
	
	public static double tan(double theta) {
		return Math.tan(theta);
	}
	
	public static float acos(float x) {
		return (float)Math.acos(x);
	}

	public static double acos(double x) {
		return Math.acos(x);
	}

	public static float asin(float y) {
		return (float)Math.asin(y);
	}
	
	public static double asin(double y) {
		return Math.asin(y);
	}

	public static float atan(float v) {
		return (float)Math.atan(v);
	}
	
	public static double atan(double v) {
		return Math.atan(v);
	}

	public static float atan2(float y, float x) {
		return (float)Math.atan2(y, x);
	}
	
	public static double atan2(double y, double x) {
		return Math.atan2(y, x);
	}
	
	public static float exp(float v) {
		return (float)Math.exp(v);
	}

	public static double exp(double v) {
		return Math.exp(v);
	}

	public static float ceil(float v) {
		return (float)Math.ceil(v);
	}
	
	public static double ceil(double v) {
		return Math.ceil(v);
	}

	public static int round(float v) {
		return Math.round(v);
	}

	public static long round(double v) {
		return Math.round(v);
	}

	public static float floor(float v) {
		return (float)Math.floor(v);
	}
	
	public static double floor(double v) {
		return Math.floor(v);
	}

	public static int floorDiv(int x, int y) {
		return Math.floorDiv(x, y);
	}

	public static long floorDiv(long x, long y) {
		return Math.floorDiv(x, y);
	}
}
