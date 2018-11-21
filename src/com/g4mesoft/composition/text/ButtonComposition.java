package com.g4mesoft.composition.text;

import java.awt.Color;

import com.g4mesoft.composition.ui.ButtonCompositionUI;
import com.g4mesoft.input.mouse.MouseButtonInput;
import com.g4mesoft.input.mouse.MouseInputListener;

public class ButtonComposition extends TextComposition {

	private String text;

	private boolean hovered;
	private boolean pressed;
	
	private Color hoveredBackground;
	private Color pressedBackground;
	
	private MouseButtonInput mouseButton;
	private ButtonCompositionListener buttonListener;
	
	public ButtonComposition() {
		this(null);
	}

	public ButtonComposition(String text) {
		this.text = text == null ? "" : text;
		
		mouseButton = MouseInputListener.MOUSE_LEFT;

		// Set UI
		setUI(new ButtonCompositionUI());
	}
	
	public void setText(String text) {
		if (text == null)
			text = "";
		this.text = text;
		
		requestRelayout();
	}
	
	public String getText() {
		return text;
	}

	public void setHovered(boolean hovered) {
		if (hovered == this.hovered)
			return;
		
		this.hovered = hovered;

		invokeButtonHoveredEvent();
	}
	
	public boolean isHovered() {
		return hovered;
	}
	
	public void setPressed(boolean pressed) {
		if (pressed == this.pressed)
			return;
		
		this.pressed = pressed;

		invokeButtonPressedEvent();
	}
	
	public boolean isPressed() {
		return pressed;
	}

	public void setMouseInput(MouseButtonInput mouseButton) {
		if (mouseButton == null)
			throw new IllegalArgumentException("Mouse button must not be null!");
		
		this.mouseButton = mouseButton;
	}
	
	public MouseButtonInput getMouseInput() {
		return mouseButton;
	}

	public void setPressedBackground(Color pb) {
		if (pb == null && pressedBackground == null)
			return;
		if (pb != null && pb.equals(pressedBackground))
			return;
		
		pressedBackground = pb;
	}

	public Color getPressedBackground() {
		return pressedBackground;
	}

	public void setHoveredBackground(Color hb) {
		if (hb == null && hoveredBackground == null)
			return;
		if (hb != null && hb.equals(hoveredBackground))
			return;
		
		hoveredBackground = hb;
	}
	
	public Color getHoveredBackground() {
		return hoveredBackground;
	}
	
	public void setButtonListener(ButtonCompositionListener listener) {
		buttonListener = listener;
	}
	
	public ButtonCompositionListener getButtonListener() {
		return buttonListener;
	}

	public void invokeButtonHoveredEvent() {
		if (buttonListener != null)
			buttonListener.buttonHovered(this);
	}

	public void invokeButtonPressedEvent() {
		if (buttonListener != null)
			buttonListener.buttonPressed(this);
	}
	
	public void invokeButtonClickedEvent() {
		if (buttonListener != null)
			buttonListener.buttonClicked(this);
	}
}
