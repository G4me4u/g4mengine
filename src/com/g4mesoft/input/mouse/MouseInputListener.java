package com.g4mesoft.input.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import com.g4mesoft.graphic.Display;

public final class MouseInputListener implements MouseListener, MouseMotionListener {

	public static final MouseButtonInput MOUSE_LEFT = new MouseButtonInput(MouseEvent.BUTTON1);
	public static final MouseButtonInput MOUSE_MIDDLE = new MouseButtonInput(MouseEvent.BUTTON2);
	public static final MouseButtonInput MOUSE_RIGHT = new MouseButtonInput(MouseEvent.BUTTON3);
	
	private static MouseInputListener instance;
	
	private List<MouseButtonInput> mouseButtons;

	private int mouseX;
	private int mouseY;
	
	private MouseInputListener() {
		mouseButtons = new ArrayList<MouseButtonInput>();
		
		addMouseInput(MOUSE_LEFT);
		addMouseInput(MOUSE_RIGHT);
		addMouseInput(MOUSE_MIDDLE);
	}
	
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

		mouseX = x;
		mouseY = y;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public int getX() {
		return mouseX;
	}
	
	public int getY() {
		return mouseY;
	}
	
	public void updateMouseButtons() {
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
	
	public void registerDisplay(Display display) {
		display.registerMouseListener(this);
		display.registerMouseMotionListener(this);
	}
	
	public void unregisterDisplay(Display display) {
		display.unregisterMouseListener(this);
		display.unregisterMouseMotionListener(this);
	}
	
	public static MouseInputListener getInstance() {
		if (instance == null)
			instance = new MouseInputListener();
		return instance;
	}
}
