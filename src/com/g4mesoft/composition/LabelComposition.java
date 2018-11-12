package com.g4mesoft.composition;

import java.awt.Color;

import com.g4mesoft.composition.ui.LabelCompositionUI;

public class LabelComposition extends Composition {

	public static final int TEXT_ALIGN_CENTER = 0;
	public static final int TEXT_ALIGN_LEFT = 1;
	public static final int TEXT_ALIGN_RIGHT = 2;
	
	protected String text;
	private Color textColor;
	
	private int textAlignment;

	public LabelComposition() {
		this(null);
	}
	
	public LabelComposition(String text) {
		this.text = (text != null ? text : "");
		
		textColor = Color.WHITE;
		textAlignment = TEXT_ALIGN_LEFT;

		// Set UI
		setUI(new LabelCompositionUI());
	}

	public void setTextColor(Color color) {
		if (color == null)
			throw new IllegalArgumentException("Color is null");

		textColor = color;
	}
	
	public Color getTextColor() {
		return textColor;
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
	
	public void setTextAlignment(int alignment) {
		textAlignment = alignment;
	}
	
	public int getTextAlignment() {
		return textAlignment;
	}
}
