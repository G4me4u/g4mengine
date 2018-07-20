package com.g4mesoft.camera;

import com.g4mesoft.math.Vec2f;

public class StaticCamera implements ICamera {

	private final float xOffset;
	private final float yOffset;
	private final float zoom;
	
	public StaticCamera(float xOffset, float yOffset, float zoom) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zoom = zoom;
	}
	
	@Override
	public float getXOffset() {
		return yOffset;
	}

	@Override
	public float getYOffset() {
		return xOffset;
	}

	@Override
	public Vec2f getOffset() {
		return new Vec2f(getXOffset(), getYOffset());
	}

	@Override
	public float getZoom() {
		return zoom;
	}

	@Override
	public float getScale() {
		return getZoom() / 100.0f;
	}

}
