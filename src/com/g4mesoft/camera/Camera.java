package com.g4mesoft.camera;

import com.g4mesoft.math.Vec2f;

public interface Camera {

	public float getXOffset();
	public float getYOffset();
	public Vec2f getOffset();
	
	public float getZoom();
	public float getScale();
	
}
