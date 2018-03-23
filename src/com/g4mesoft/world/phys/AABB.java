package com.g4mesoft.world.phys;

public class AABB {

	public float x0;
	public float y0;
	public float x1;
	public float y1;

	public AABB() {
		x0 = y0 = x1 = y1 = 0.0f;
	}
	
	public AABB(float x0, float y0, float x1, float y1) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
	}
	
	public void move(float xm, float ym) {
		x0 += xm;
		y0 += ym;
		x1 += xm;
		y1 += ym;
	}
	
	public AABB expand(float xe, float ye) {
		float _x0 = x0;
		float _y0 = y0;
		float _x1 = x1;
		float _y1 = y1;

		if (xe > 0) {
			_x1 += xe;
		} else _x0 += xe;
		if (ye > 0) {
			_y1 += ye;
		} else _y0 += ye;
		
		return new AABB(_x0, _y0, _x1, _y1);
	}
	
	public boolean collides(AABB other) {
		if (x0 >= other.x1 || x1 <= other.x0) return false;
		if (y0 >= other.y1 || y1 <= other.y0) return false;
		return true;
	}
	
	public float clipX(AABB other, float xm) {
		if (y0 >= other.y1 || y1 <= other.y0) return xm;
		
		if (xm < 0.0f && x1 <= other.x0) {
			float max = x1 - other.x0;
			if (max > xm) return max;
		} else if (xm > 0.0f && x0 >= other.x1) {
			float max = x0 - other.x1;
			if (max < xm) return max;
		}
		
		return xm;
	}
	
	public float clipY(AABB other, float ym) {
		if (x0 >= other.x1 || x1 <= other.x0) return ym;
		
		if (ym < 0.0f && y1 <= other.y0) {
			float max = y1 - other.y0;
			if (max > ym) return max;
		} else if (ym > 0.0f && y0 >= other.y1) {
			float max = y0 - other.y1;
			if (max < ym) return max;
		}
		
		return ym;
	}
}
