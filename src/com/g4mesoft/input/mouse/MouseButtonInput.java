package com.g4mesoft.input.mouse;

import java.awt.event.MouseEvent;

import com.g4mesoft.input.Input;

public class MouseButtonInput extends Input {

	private int button;

	private boolean pressed;
	private boolean wasPressed;
	
	private long activationTime;
	
	private int clickX;
	private int clickY;
	
	private boolean dragged;
	private boolean wasDragged;
	
	private int dragX;
	private int dragY;
	private int prevX;
	private int prevY;
	
	public MouseButtonInput(int button) {
		this.button = button;
	}

	public void update() {
		wasPressed = pressed;
		wasDragged = dragged;
	}

	public void reset() {
		pressed = false;
		wasPressed = false;
		
		dragged = false;
		wasDragged = false;
	}
	
	public void mousePressed(int button, int x, int y) {
		if (button != this.button)
			return;

		if (!pressed) {
			wasPressed = false;
			pressed = true;
			
			clickX = x;
			clickY = y;

			dragX = 0;
			dragY = 0;
			
			activationTime = System.currentTimeMillis();
		}
	}

	public void mouseReleased(int button, int x, int y) {
		if (button != this.button)
			return;
		
		pressed = false;
		dragged = false;

		dragX = 0;
		dragY = 0;
	}

	public void mouseDragged(int button, int x, int y) {
		// There is a bug with java.awt where the
		// drag events wont carry a button field.
		// Fix this by testing if button != NOBUTTON.
		if (button != this.button && button != MouseEvent.NOBUTTON)
			return;
		
		// Fix for missing button field. Just make sure
		// this button is held.
		if (!pressed)
			return;
		
		if (!dragged) {
			wasDragged = false;
			dragged = true;
		}

		dragX += x - prevX;
		dragY += y - prevY;
		prevX = x;
		prevY = y;
	}

	public void mouseGrapMoved(int x, int y) {
		prevX = x;
		prevY = y;
	}
	
	public int getButton() {
		return button;
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	public boolean wasPressed() {
		return wasPressed;
	}
	
	public final boolean isClicked() {
		return pressed && !wasPressed;
	}
	
	public final boolean isReleased() {
		return !pressed && wasPressed;
	}

	public int getClickX() {
		return clickX;
	}

	public int getClickY() {
		return clickY;
	}
	
	public boolean isDragged() {
		return dragged;
	}
	
	public boolean wasDragged() {
		return wasDragged;
	}
	
	public int getDragX() {
		return dragX;
	}

	public int getDragY() {
		return dragY;
	}
	
	@Override
	public boolean isActive() {
		return pressed;
	}

	@Override
	public long getActivationTime() {
		return activationTime;
	}
}
