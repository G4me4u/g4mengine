package com.g4mesoft.input.mouse;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import com.g4mesoft.graphic.Display;

public final class MouseInputListener {

	public static final MouseButtonInput MOUSE_LEFT = new MouseButtonInput(MouseEvent.BUTTON1);
	public static final MouseButtonInput MOUSE_MIDDLE = new MouseButtonInput(MouseEvent.BUTTON2);
	public static final MouseButtonInput MOUSE_RIGHT = new MouseButtonInput(MouseEvent.BUTTON3);
	
	private static MouseInputListener instance;
	
	private List<MouseButtonInput> mouseButtons;
	private MouseButtonListener buttonListener;
	
	private int mouseX;
	private int mouseY;
	
	private int deltaX;
	private int deltaY;
	
	private Robot robot;
	private boolean focused;
	private boolean grabbed;

	private Display display;
	
	private MouseInputListener() {
		mouseButtons = new ArrayList<MouseButtonInput>();
		buttonListener = new MouseButtonListener();
		
		addMouseInput(MOUSE_LEFT);
		addMouseInput(MOUSE_RIGHT);
		addMouseInput(MOUSE_MIDDLE);
	}
	
	private void mouseMoved(Component origin, int x, int y) {
		deltaX += x - mouseX;
		deltaY += y - mouseY;
		mouseX = x;
		mouseY = y;
		
		checkDisplayFocus(x, y);
		
		if (grabbed && focused) {
			ensureCursorIsInCenter();
		}
	}
	
	private void checkDisplayFocus(int x, int y) {
		if (display != null && display.isFocused() && !focused) {
			Canvas canvas = display.getCanvas();
			
			if (canvas != null && canvas.contains(x, y))
				setDisplayFocused(true);
		}
	}

	private void ensureCursorIsInCenter() {
		if (display != null) {
			Canvas canvas = display.getCanvas();
			
			Point screenLocation = canvas.getLocationOnScreen();
			
			int xc = canvas.getWidth() / 2;
			int yc = canvas.getHeight() / 2;

			int sx = xc + screenLocation.x;
			int sy = yc + screenLocation.y;

			// We would use mouseX and mouseY as our
			// primary cursor coordinates. But when
			// the user moves outside the window, these
			// values may not be accurate. Instead we
			// get them from the MouseInfo class.
			Point currentLocation = MouseInfo.getPointerInfo().getLocation();
			if (sx != currentLocation.x || sy != currentLocation.y) {
				// Ensure the mouse moved event from the
				// robot wont change the deltaX / deltaY
				// values.
				mouseX = xc;
				mouseY = yc;
				
				for (MouseButtonInput mouseButton : mouseButtons)
					mouseButton.mouseGrapMoved(xc, yc);
				
				robot.mouseMove(sx, sy);
			}
		}
	}
	
	private void setDisplayFocused(boolean focused) {
		this.focused = focused;
		
		if (display != null && grabbed) {
			if (focused) {
				ensureCursorIsInCenter();
				display.disableCursor();
			} else {
				display.enableCursor();
			}
		}
	}
	
	public void updateMouseButtons() {
		if (display == null || !display.isFocused()) {
			setDisplayFocused(false);
		} else if (focused && grabbed) {
			// Sometimes the cursor will go outside
			// of bounds, if the user moves the mouse
			// very quickly. To fix this we also
			// ensure the cursor is in the center every
			// tick. However, this might not be the best 
			// solution, if the tick-rate is low.
			ensureCursorIsInCenter();
		}
		
		deltaX = 0;
		deltaY = 0;
		
		for (MouseButtonInput mouseButton : mouseButtons)
			mouseButton.update();
	}

	public void resetMouseButtons() {
		for (MouseButtonInput mouseButton : mouseButtons)
			mouseButton.reset();
	}
	
	public boolean addMouseInput(MouseButtonInput mouseButton) {
		if (mouseButtons.contains(mouseButton))
			return false;
		return mouseButtons.add(mouseButton);
	}
	
	public boolean removeMouseInput(MouseButtonInput mouseButton) {
		return mouseButtons.remove(mouseButton);
	}
	
	public int getX() {
		return mouseX;
	}
	
	public int getY() {
		return mouseY;
	}
	
	public int getDeltaX() {
		return deltaX;
	}

	public int getDeltaY() {
		return deltaY;
	}
	
	public void registerDisplay(Display display) {
		this.display = display;
		
		display.registerMouseListener(buttonListener);
		display.registerMouseMotionListener(buttonListener);
		
		// Make sure the cursor updates
		if (grabbed && focused) {
			display.disableCursor();
		}
	}
	
	public void unregisterDisplay(Display display) {
		display.unregisterMouseListener(buttonListener);
		display.unregisterMouseMotionListener(buttonListener);

		display.enableCursor();
		
		this.display = null;
	}
	
	public void setGrabbed(boolean grabbed) {
		if (robot == null && grabbed) {
			try {
				robot = new Robot();
			} catch (AWTException e) {
				throw new RuntimeException("Grabbing not supported on current system.", e);
			}
		}

		this.grabbed = grabbed;
		
		if (display != null) {
			if (grabbed) {
				focused = display.isFocused();
				display.disableCursor();
			} else {
				display.enableCursor();
			}
		}
	}
	
	public boolean isGrabbed() {
		return grabbed;
	}
	
	public boolean isGrabActive() {
		return focused && grabbed;
	}
	
	public static MouseInputListener getInstance() {
		if (instance == null)
			instance = new MouseInputListener();
		return instance;
	}
	
	private class MouseButtonListener implements MouseListener, MouseMotionListener {
		
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			int button = e.getButton();
			int x = e.getX();
			int y = e.getY();
			
			for (MouseButtonInput mouseButton : mouseButtons)
				mouseButton.mousePressed(button, x, y);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			int button = e.getButton();
			int x = e.getX();
			int y = e.getY();
			
			for (MouseButtonInput mouseButton : mouseButtons)
				mouseButton.mouseReleased(button, x, y);

			MouseInputListener.this.checkDisplayFocus(x, y);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			int button = e.getButton();
			int x = e.getX();
			int y = e.getY();
			
			for (MouseButtonInput mouseButton : mouseButtons)
				mouseButton.mouseDragged(button, x, y);

			MouseInputListener.this.mouseMoved(e.getComponent(), x, y);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			MouseInputListener.this.mouseMoved(e.getComponent(), e.getX(), e.getY());
		}
	}
}
