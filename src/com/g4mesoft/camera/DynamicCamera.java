package com.g4mesoft.camera;

import com.g4mesoft.math.Vec2f;

public class DynamicCamera implements ICamera {

	private CameraPose pose;

	private float xMove;
	private float yMove;
	private float deltaZoom;
	
	private float viewWidth;
	private float viewHeight;
	
	private float minZoom;
	private float maxZoom;

	private boolean zoomToCenter;
	
	private CameraPose smoothingPose;
	private float poseSmoothingFactor;
	private float poseSmoothingTimer;
	
	public DynamicCamera() {
		pose = new CameraPose();
		
		xMove = 0.0f;
		yMove = 0.0f;
		deltaZoom = 0.0f;
		
		viewWidth = 0.0f;
		viewHeight = 0.0f;

		minZoom = Float.MIN_VALUE;
		maxZoom = Float.MAX_VALUE;
	
		zoomToCenter = false;
	}
	
	public void update() {
		pose.xOffset += xMove;
		pose.yOffset += yMove;
		xMove = 0.0f;
		yMove = 0.0f;
		
		pose.zoom += deltaZoom;
		deltaZoom = 0.0f;
	
		animatePose();
	}
	
	protected void animatePose() {
		// If we're done animating the
		// smoothing pose is null.
		if (smoothingPose == null)
			return;

		// No reason to animate, if there's
		// nothing to change.
		if (!smoothingPose.offsetSet && !smoothingPose.zoomSet) {
			smoothingPose = null;
			return;
		}
		
		float oldSmoothingTimer = poseSmoothingTimer;
		poseSmoothingTimer += poseSmoothingFactor;
		
		// We might change the smoothingPose
		// field. Make a copy to be sure we
		// don't get a NullPointerException
		// when the animation finishes.
		CameraPose sPose = smoothingPose;

		float c;
		if (poseSmoothingTimer >= 1.0f) {
			c = 1.0f;
			
			// If we're ever at this point,
			// the animation has completed.
			smoothingPose = null;
		} else {
			c = poseSmoothingFactor / (1.0f - oldSmoothingTimer);
		}
		
		if (sPose.offsetSet) {
			float dx = sPose.xOffset - pose.xOffset;
			float dy = sPose.yOffset - pose.yOffset;

			moveXOffset(dx * c);
			moveYOffset(dy * c);
		}

		if (sPose.zoomSet) {
			float dz = sPose.zoom - pose.zoom;

			addZoom(dz * c);
		}
	}
	
	public void setXOffset(float xOffset) {
		moveXOffset(xOffset - pose.xOffset);
	}

	public void setCenterX(float xCenter) {
		setXOffset(xCenter - viewWidth * 0.5f);
	}

	public void setYOffset(float yOffset) {
		moveYOffset(yOffset - pose.yOffset);
	}
	
	public void setCenterY(float yCenter) {
		setYOffset(yCenter - viewHeight * 0.5f);
	}

	public void setOffset(float xOffset, float yOffset) {
		moveOffset(xOffset - pose.xOffset, yOffset - pose.yOffset);
	}
	
	public void setCenter(float xCenter, float yCenter) {
		setOffset(xCenter - viewWidth * 0.5f, yCenter - viewHeight * 0.5f);
	}

	public void moveXOffset(float xMove) {
		// The view has already been
		// centered around the center
		// of the bounding box.
		if (viewWidth > pose.bx1 - pose.bx0)
			return;
		
		this.xMove += xMove;
		
		checkBoundsX();
	}
	
	private void checkBoundsX() {
		if (pose.xOffset + this.xMove < pose.bx0) {
			this.xMove = pose.bx0 - pose.xOffset;
		} else if (pose.xOffset + this.xMove + viewWidth > pose.bx1) {
			this.xMove = pose.bx1 - viewWidth - pose.xOffset;
		}
	}

	public void moveYOffset(float yMove) {
		// The view has already been
		// centered around the center
		// of the bounding box.
		if (viewHeight > pose.by1 - pose.by0)
			return;
		
		this.yMove += yMove;
		
		checkBoundsY();
	}

	private void checkBoundsY() {
		if (pose.yOffset + this.yMove < pose.by0) {
			this.yMove = pose.by0 - pose.yOffset;
		} else if (pose.yOffset + this.yMove + viewHeight > pose.by1) {
			this.yMove = pose.by1 - viewHeight - pose.yOffset;
		}
	}
	
	public void moveOffset(float xMove, float yMove) {
		moveXOffset(xMove);
		moveYOffset(yMove);
	}
	
	public void setPose(CameraPose pose, float smoothFactor) {
		if (pose.screenSizeSet)
			setScreenSize(pose.screenWidth, pose.screenHeight);
		if (pose.boundsSet)
			setBounds(pose.bx0, pose.by0, pose.bx1, pose.by1);
		
		smoothingPose = pose;
		poseSmoothingFactor = smoothFactor;
		poseSmoothingTimer = 0.0f;
		
		animatePose();
	}
	
	public CameraPose getCameraPose() {
		return pose.copyPose();
	}
	
	public void setScreenSize(float screenWidth, float screenHeight) {
		pose.screenWidth = screenWidth;
		pose.screenHeight = screenHeight;
		
		updateViewport();
	}
	
	protected void updateViewport() {
		float s = (pose.zoom + deltaZoom) * 0.01f;
		
		viewWidth = pose.screenWidth / s;
		viewHeight = pose.screenHeight / s;
		
		updateBounds();
	}
	
	public void setScale(float scale) {
		setZoom(scale * 100.0f);
	}
	
	public void setZoom(float zoom) {
		if (zoom > maxZoom) {
			deltaZoom = maxZoom - pose.zoom;
		} else if (zoom < minZoom) {
			deltaZoom = minZoom - pose.zoom;
		} else {
			deltaZoom = zoom - pose.zoom;
		}
		
		if (zoomToCenter) {
			float ns = (pose.zoom + deltaZoom) * 0.01f;
			float nw = pose.screenWidth / ns;
			float nh = pose.screenHeight / ns;
			
			xMove += (viewWidth - nw) * 0.5f;
			yMove += (viewHeight - nh) * 0.5f;
		}
	
		updateViewport();
	}
	
	public void addZoom(float deltaZoom) {
		setZoom(pose.zoom + this.deltaZoom + deltaZoom);
	}
	
	public void setMaxZoom(float maxZoom) {
		this.maxZoom = maxZoom;

		if (maxZoom < pose.zoom + deltaZoom)
			setZoom(maxZoom);
	}

	public void setMinZoom(float minZoom) {
		this.minZoom = minZoom;
		
		if (minZoom > pose.zoom + deltaZoom)
			setZoom(minZoom);
	}
	
	public void setZoomToCenter(boolean zoomToCenter) {
		this.zoomToCenter = zoomToCenter;
	}
	
	public void setBounds(float bx0, float by0, float bx1, float by1) {
		pose.bx0 = bx0;
		pose.by0 = by0;
		pose.bx1 = bx1;
		pose.by1 = by1;
		
		updateBounds();
	}
	
	protected void updateBounds() {
		float bw = pose.bx1 - pose.bx0;
		float bh = pose.by1 - pose.by0;
		
		if (viewWidth > bw) {
			xMove = pose.bx0 + (bw - viewWidth) * 0.5f - pose.xOffset;
		} else {
			checkBoundsX();
		}
		if (viewHeight > bh) {
			yMove = pose.by0 + (bh - viewHeight) * 0.5f - pose.yOffset;
		} else {
			checkBoundsY();
		}
	}
	
	@Override
	public float getXOffset() {
		return pose.xOffset;
	}
	
	public float getCenterX() {
		return pose.xOffset + viewWidth * 0.5f;
	}

	@Override
	public float getYOffset() {
		return pose.yOffset;
	}

	public float getCenterY() {
		return pose.yOffset + viewHeight * 0.5f;
	}
	
	@Override
	public Vec2f getOffset() {
		return new Vec2f(getXOffset(), getYOffset());
	}
	
	public Vec2f getCenter() {
		return new Vec2f(getCenterX(), getCenterY());
	}

	public float getXOffset(float dt) {
		return pose.xOffset + xMove * dt;
	}

	public float getCenterX(float dt) {
		return getXOffset(dt) + viewWidth * 0.5f;
	}
	
	public float getYOffset(float dt) {
		return pose.yOffset + yMove * dt;
	}

	public float getCenterY(float dt) {
		return getYOffset(dt) + viewHeight * 0.5f;
	}

	public Vec2f getOffset(float dt) {
		return new Vec2f(getXOffset(dt), getYOffset(dt));
	}

	public Vec2f getCenter(float dt) {
		return new Vec2f(getCenterX(dt), getCenterY(dt));
	}

	@Override
	public float getZoom() {
		return pose.zoom;
	}

	@Override
	public float getScale() {
		return pose.zoom * 0.01f;
	}
	
	public float getScale(float dt) {
		return (pose.zoom + deltaZoom * dt) * 0.01f;
	}
	
	public float getScreenWidth() {
		return pose.screenWidth;
	}
	
	public float getScreenHeight() {
		return pose.screenHeight;
	}
	
	public float getViewWidth() {
		return viewWidth;
	}
	
	public float getViewHeight() {
		return viewHeight;
	}
	
	public static final class CameraPose {
		
		private float xOffset;
		private float yOffset;
		private float zoom;

		private float screenWidth;
		private float screenHeight;

		private float bx0;
		private float by0;
		private float bx1;
		private float by1;
		
		private boolean offsetSet;
		private boolean zoomSet;
		private boolean screenSizeSet;
		private boolean boundsSet;
		
		public CameraPose(float xOffset, float yOffset) {
			this();
			
			setOffset(xOffset, yOffset);
		}

		public CameraPose(float xOffset, float yOffset, float zoom) {
			this();
			
			setOffset(xOffset, yOffset);
			setZoom(zoom);
		}

		public CameraPose(float xOffset, float yOffset, float zoom, float screenWidth, float screenHeight) {
			this();
			
			setOffset(xOffset, yOffset);
			setZoom(zoom);
			setScreenSize(screenWidth, screenHeight);
		}
		
		private CameraPose() {
			xOffset = 0.0f;
			yOffset = 0.0f;
			zoom = 100.0f;

			screenWidth = 0.0f;
			screenHeight = 0.0f;

			bx0 = Float.NEGATIVE_INFINITY;
			by0 = Float.NEGATIVE_INFINITY;
			bx1 = Float.POSITIVE_INFINITY;
			by1 = Float.POSITIVE_INFINITY;
		
			offsetSet = false;
			zoomSet = false;
			screenSizeSet = false;
			boundsSet = false;
		}
		
		public void setOffset(float xOffset, float yOffset) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			
			offsetSet = true;
		}

		public void setZoom(float zoom) {
			this.zoom = zoom;
			
			zoomSet = true;
		}
		
		public void setScreenSize(float screenWidth, float screenHeight) {
			this.screenWidth = screenWidth;
			this.screenHeight = screenHeight;
			
			screenSizeSet = true;
		}
		
		public void setBounds(float bx0, float by0, float bx1, float by1) {
			this.bx0 = bx0;
			this.by0 = by0;
			this.bx1 = bx1;
			this.by1 = by1;
			
			boundsSet = true;
		}
		
		public CameraPose copyPose() {
			CameraPose pose = new CameraPose();
			if (offsetSet)
				pose.setOffset(xOffset, yOffset);
			if (zoomSet)
				pose.setZoom(zoom);
			if (screenSizeSet)
				pose.setScreenSize(screenWidth, screenHeight);
			if (boundsSet)
				pose.setBounds(bx0, by0, bx1, by1);
			return pose;
		}
	}
}
