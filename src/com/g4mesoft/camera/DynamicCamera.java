package com.g4mesoft.camera;

import java.util.Random;

import com.g4mesoft.math.Vec2f;

public class DynamicCamera implements ICamera {

	private static final Random random = new Random();

	private float xoffs;
	private float yoffs;
	private float staticX;
	private float staticY;
	private float shakeX;
	private float shakeY;
	
	private float xmove;
	private float ymove;
	
	private float width;
	private float height;
	private float viewWidth;
	private float viewHeight;
	
	private float bx0;
	private float by0;
	private float bx1;
	private float by1;

	private int shakeAmount;

	private float zoomChange;
	private float zoom;
	private float minZoom;
	private float maxZoom;
	private boolean zoomToCenter;

	public DynamicCamera() {
		xoffs = 0.0f;
		yoffs = 0.0f;
		staticX = 0.0f;
		staticY = 0.0f;

		width = 0.0f;
		height = 0.0f;
		viewWidth = 0.0f;
		viewHeight = 0.0f;
		
		bx0 = Float.NEGATIVE_INFINITY;
		by0 = Float.NEGATIVE_INFINITY;
		bx1 = Float.POSITIVE_INFINITY;
		by1 = Float.POSITIVE_INFINITY;

		minZoom = Float.MIN_VALUE;
		maxZoom = Float.MAX_VALUE;
		zoom = 100.0f;
	}

	public void update() {
		finishMovement();
		finishZoom();
		
		if (shakeAmount > 0) {
			shakeAmount--;
			
			shakeX = random.nextFloat() - 0.5f;
			shakeY = random.nextFloat() - 0.5f;
		} else {
			shakeX = 0.0f;
			shakeY = 0.0f;
		}
	}

	private void finishMovement() {
		xoffs += xmove;
		yoffs += ymove;
		xmove = 0.0f;
		ymove = 0.0f;
		
		if (Float.isNaN(xoffs))
			xoffs = bx0;
		if (Float.isNaN(yoffs))
			yoffs = by0;
	}
	
	private void fixBounds() {
		if (xoffs + xmove < bx0) {
			xmove = bx0 - xoffs;
		} else if (xoffs + xmove + viewWidth > bx1) {
			xmove = bx1 - xoffs - viewWidth;
		}

		if (yoffs + ymove < by0) {
			ymove = by0 - yoffs;
		} else if (yoffs + ymove + viewHeight > by1) {
			ymove = by1 - yoffs - viewHeight;
		}

		if (Float.isNaN(xmove))
			xmove = 0.0f;
		if (Float.isNaN(ymove))
			ymove = 0.0f;
	}
	
	public void move(float xm, float ym) {
		xmove += xm;
		ymove += ym;

		fixBounds();
	}

	public void moveTo(float x, float y) {
		move(x - xoffs - xmove - staticX, y - yoffs - ymove - staticY);
	}

	public void moveToCenter(float xc, float yc) {
		moveTo(xc - viewWidth / 2f, yc - viewHeight / 2f);
	}

	public void setOffset(float xo, float yo) {
		xmove = 0.0f;
		ymove = 0.0f;
		
		xo -= staticX;
		yo -= staticY;

		if (xo < bx0) {
			xoffs = bx0;
		} else if (xo + viewWidth > bx1) {
			xoffs = bx1 - viewWidth;
		} else {
			xoffs = xo;
		}

		if (yo < by0) {
			yoffs = by0;
		} else if (yo + viewHeight > by1) {
			yoffs = by1 - viewHeight;
		} else {
			yoffs = yo;
		}
	}
	
	public void setOffsetCenter(float xc, float yc) {
		setOffset(xc - viewWidth / 2f, yc - viewHeight / 2f);
	}

	public float getXOffset(boolean shake) {
		return getXOffset(1.0f, shake);
	}

	public float getYOffset(boolean shake) {
		return getYOffset(1.0f, shake);
	}
	
	public float getXOffset(float dt, boolean shake) {
		if (shake)
			return xoffs + xmove * dt + staticX + shakeX;

		return xoffs + xmove * dt + staticX;
	}

	public float getYOffset(float dt, boolean shake) {
		if (shake)
			return yoffs + ymove * dt + staticY + shakeY;
		
		return yoffs + ymove * dt + staticY;
	}
	
	public float getXOffset() {
		return getXOffset(true);
	}

	public float getYOffset() {
		return getYOffset(true);
	}
	
	public float getXOffset(float dt) {
		return getXOffset(dt, true);
	}

	public float getYOffset(float dt) {
		return getYOffset(dt, true);
	}

	public Vec2f getOffset(float dt, boolean shake) {
		return new Vec2f(getXOffset(dt, shake), getYOffset(dt, shake));
	}
	
	public Vec2f getOffset(float dt) {
		return getOffset(dt, true);
	}
	
	public Vec2f getOffset(boolean shake) {
		return getOffset(1.0f, shake);
	}
	
	public Vec2f getOffset() {
		return getOffset(1.0f, true);
	}
	
	public float getCenterX(boolean shake) {
		return getCenterX(1.0f, shake);
	}

	public float getCenterY(boolean shake) {
		return getCenterY(1.0f, shake);
	}
	
	public float getCenterX(float dt, boolean shake) {
		return getXOffset(dt, shake) + viewWidth / 2.0f;
	}

	public float getCenterY(float dt, boolean shake) {
		return getYOffset(dt, shake) + viewHeight / 2.0f;
	}

	public float getCenterX() {
		return getCenterX(true);
	}

	public float getCenterY() {
		return getCenterY(true);
	}
	
	public float getCenterX(float dt) {
		return getCenterX(dt, true);
	}

	public float getCenterY(float dt) {
		return getCenterX(dt, true);
	}
	
	public Vec2f getCenter(float dt, boolean shake) {
		return new Vec2f(getCenterX(dt, shake), getCenterY(dt, shake));
	}
	
	public Vec2f getCenter(float dt) {
		return getCenter(dt, true);
	}
	
	public Vec2f getCenter(boolean shake) {
		return getCenter(1.0f, shake);
	}
	
	public Vec2f getCenter() {
		return getCenter(true);
	}
	
	public void setStaticOffsetX(float staticX) {
		this.staticX = staticX;
	}

	public void setStaticOffsetY(float staticY) {
		this.staticY = staticY;
	}

	public void setBounds(float x0, float y0, float x1, float y1) {
		bx0 = x0;
		by0 = y0;
		bx1 = x1;
		by1 = y1;

		fixBounds();
	}

	public void addScreenShake(int amount) {
		if (amount > 0) {
			this.shakeAmount += amount;
		}
	}

	private void finishZoom() {
		this.zoom += zoomChange;
		zoomChange = 0;
		
		if (Float.isNaN(zoom))
			zoom = (minZoom + maxZoom) / 2.0f;
	}
	
	public void setZoomInstant(float zoom) {
		setZoom(zoom);
		
		finishZoom();
	}

	public void setZoom(float zoom) {
		addZoom(zoom - this.zoom - this.zoomChange);
	}

	public void addZoom(float zoom) {
		float xc = getCenterX(false);
		float yc = getCenterY(false);

		zoomChange += zoom;

		if (this.zoom + zoomChange > maxZoom) {
			zoomChange = maxZoom - this.zoom;
		} else if (this.zoom + zoomChange < minZoom) {
			zoomChange = minZoom - this.zoom;
		}
		
		if (zoomChange == 0.0f) return;

		viewWidth = width / getScale();
		viewHeight = height / getScale();

		if (zoomToCenter) {
			moveToCenter(xc, yc);
		} else {
			fixBounds();
		}
	}
	
	public void setViewport(float x0, float y0, float x1, float y1) {
		moveTo(x0, y0);

		if (x0 > x1 || y0 > y1) {
			setZoom(maxZoom);
		} else if (viewWidth <= 0.0f || viewHeight <= 0.0f) {
			setZoom(minZoom);
		} else {
			x0 = xoffs;
			y0 = yoffs;
			
			float zx = viewWidth / (x1 - x0);
			float zy = viewHeight / (y1 - y0);
			
			setZoom(zx < zy ? zx : zy);
		}
	}

	public void setViewportInstant(float x0, float y0, float x1, float y1) {
		setViewport(x0, y0, x1, y1);
		
		finishMovement();
		finishZoom();
	}
	
	public float getZoom() {
		return getZoom(1.0f);
	}

	public float getZoom(float dt) {
		return zoom + zoomChange * dt;
	}

	public float getZoomChangeThisUpdate() {
		return zoomChange;
	}

	public boolean zoomedThisUpdate() {
		return zoomChange != 0.0f;
	}

	public boolean movedThisUpdate() {
		return xmove != 0.0f || ymove != 0.0f;
	}

	public float getScale() {
		return getScale(1.0f);
	}

	public float getScale(float dt) {
		return getZoom(dt) / 100.0f;
	}

	public void setMinZoom(float minZoom) {
		this.minZoom = minZoom;
	}

	public float getMinZoom() {
		return minZoom;
	}

	public void setMaxZoom(float maxZoom) {
		this.maxZoom = maxZoom;
	}

	public float getMaxZoom() {
		return maxZoom;
	}

	public void setZoomToCenter(boolean zoomToCenter) {
		this.zoomToCenter = zoomToCenter;
	}

	public boolean isZoomToCenter() {
		return zoomToCenter;
	}
	
	public void setWidth(float width) {
		this.width = width;
		this.viewWidth = width / getScale();
	}

	public float getWidth() {
		return width;
	}
	
	public float getViewWidth() {
		return viewWidth;
	}
	
	public void setHeight(float height) {
		this.height = height;
		this.viewHeight = height / getScale();
	}

	public float getHeight() {
		return height;
	}
	
	public float getViewHeight() {
		return viewHeight;
	}
}
