package com.g4mesoft.composition.ui;

import java.awt.Color;
import java.awt.Rectangle;

import com.g4mesoft.composition.Composition;
import com.g4mesoft.composition.text.ButtonComposition;
import com.g4mesoft.composition.text.LabelComposition;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.input.mouse.MouseButtonInput;
import com.g4mesoft.input.mouse.MouseInputListener;
import com.g4mesoft.math.Vec2i;

public class ButtonCompositionUI extends TextCompositionUI {

	protected ButtonComposition button;
	
	protected String trimmedText;
	protected Rectangle buttonBounds;
	
	@Override
	public void bindUI(Composition composition) {
		if (button != null)
			throw new IllegalStateException("UI already bound!");
		
		button = (ButtonComposition)composition;

		// Install defaults.
		button.setTextColor(Color.WHITE);
		button.setBackground(Color.BLACK);
		button.setHoveredBackground(Color.DARK_GRAY);
		button.setPressedBackground(Color.GRAY);
		
		button.setBorderWidth(1);
		button.setBorder(Composition.BORDER_ALL);
		button.setBorderColor(Color.WHITE);
		
		button.setTextAlignment(LabelComposition.TEXT_ALIGN_CENTER);
		
		button.setMouseInput(MouseInputListener.MOUSE_LEFT);
		
		buttonBounds = new Rectangle();
	}

	@Override
	public void unbindUI(Composition composition) {
		if (button == null)
			throw new IllegalStateException("UI not bound!");
		
		buttonBounds = null;
		trimmedText = null;
		
		button = null;
	}
	
	@Override
	public void layoutChanged(IRenderingContext2D context) {
		String text = button.getText();
		
		// We don't need to trim an
		// empty string!
		if (text != null && !text.isEmpty()) {
			// Trim text
			trimmedText = trimText(context, text, button.getWidth());
		} else {
			trimmedText = null;
		}
		
		buttonBounds.setLocation(button.getX(), button.getY());
		buttonBounds.setSize(button.getWidth(), button.getHeight());
	}

	@Override
	public void update() {
		MouseInputListener mouse = MouseInputListener.getInstance();
		MouseButtonInput mouseInput = button.getMouseInput();

		int mx = mouse.getX();
		int my = mouse.getY();
	
		boolean hovered = button.isInBounds(mx, my);
		
		// Make sure the original press was
		// in bounds.
		int cx = mouseInput.getClickX();
		int cy = mouseInput.getClickY();
		
		boolean leftPressed = mouseInput.isPressed();
		if (leftPressed && !button.isInBounds(cx, cy))
			hovered = false;
		
		button.setHovered(hovered);

		boolean wasPressed = button.isPressed();
		button.setPressed(hovered && leftPressed);
		
		if (hovered && wasPressed && !leftPressed)
			button.invokeButtonClickedEvent();
	}

	@Override
	public void render(IRenderer2D renderer, float dt) {
		Color background = null;
		
		if (button.isPressed()) {
			background = button.getPressedBackground();
		} else if (button.isHovered()) {
			background = button.getHoveredBackground();
		} else {
			background = button.getBackground();
		}

		if (background != null) {
			renderer.setColor(background);
			renderer.fillRect(button.getX(), button.getY(), button.getWidth(), button.getHeight());
		}
		
		if (trimmedText != null) {
			renderer.setColor(button.getTextColor());
			drawAlignedText(renderer, trimmedText, button.getTextAlignment(), buttonBounds);
		}
		
		drawBorder(renderer, button);
	}

	@Override
	public Vec2i getPreferredSize(IRenderingContext2D context) {
		return getPreferredSize(context, button.getText());
	}
}
