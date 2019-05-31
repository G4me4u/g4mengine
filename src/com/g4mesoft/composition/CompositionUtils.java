package com.g4mesoft.composition;

import com.g4mesoft.math.MathUtils;

public final class CompositionUtils {

	public static int getBoundedX(int x, Composition c) {
		return MathUtils.clamp(x, c.getX(), c.getX() + c.getWidth());
	}

	public static int getBoundedY(int y, Composition c) {
		return MathUtils.clamp(y, c.getY(), c.getY() + c.getHeight());
	}
}
