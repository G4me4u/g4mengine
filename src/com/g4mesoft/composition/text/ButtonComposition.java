package com.g4mesoft.composition.text;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.g4mesoft.composition.ui.ButtonCompositionUI;
import com.g4mesoft.graphic.GColor;
import com.g4mesoft.input.mouse.MouseButtonInput;
import com.g4mesoft.input.mouse.MouseInputListener;

public class ButtonComposition extends TextComposition {

	private String text;

	private boolean hovered;
	private boolean pressed;
	
	private GColor hoveredBackground;
	private GColor pressedBackground;
	
	private MouseButtonInput mouseButton;
	private Set<IButtonCompositionListener> buttonListeners;
	
	public ButtonComposition() {
		this(null);
	}

	public ButtonComposition(String text) {
		this.text = text == null ? "" : text;
		
		mouseButton = MouseInputListener.MOUSE_LEFT;
		buttonListeners = new HashSet<IButtonCompositionListener>();

		// Set UI
		setUI(new ButtonCompositionUI());
	}
	
	public void setText(String text) {
		if (text == null)
			text = "";
		this.text = text;
		
		requestRelayout(true);
	}
	
	public String getText() {
		return text;
	}

	public void setHovered(boolean hovered) {
		if (hovered == this.hovered)
			return;
		
		this.hovered = hovered;
	}
	
	public boolean isHovered() {
		return hovered;
	}
	
	public void setPressed(boolean pressed) {
		if (pressed == this.pressed)
			return;
		
		this.pressed = pressed;
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

	public void setPressedBackground(GColor pb) {
		if (pb == null && pressedBackground == null)
			return;
		if (pb != null && pb.equals(pressedBackground))
			return;
		
		pressedBackground = pb;
	}

	public GColor getPressedBackground() {
		return pressedBackground;
	}

	public void setHoveredBackground(GColor hb) {
		if (hb == null && hoveredBackground == null)
			return;
		if (hb != null && hb.equals(hoveredBackground))
			return;
		
		hoveredBackground = hb;
	}
	
	public GColor getHoveredBackground() {
		return hoveredBackground;
	}
	
	public void addButtonListener(IButtonCompositionListener listener) {
		buttonListeners.add(listener);
	}
	
	public Set<IButtonCompositionListener> getButtonListener() {
		return Collections.unmodifiableSet(buttonListeners);
	}

	public void invokeButtonClickedEvent() {
		for (IButtonCompositionListener listener : buttonListeners)
			listener.buttonClicked(this);
	}
}
