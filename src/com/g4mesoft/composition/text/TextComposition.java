package com.g4mesoft.composition.text;

import java.awt.Color;

import com.g4mesoft.composition.Composition;

public abstract class TextComposition extends Composition {

	public static final int TEXT_ALIGN_CENTER = 0;
	public static final int TEXT_ALIGN_LEFT = 1;
	public static final int TEXT_ALIGN_RIGHT = 2;
	
	protected int textAlignment;
	protected Color textColor;
	
	public TextComposition() {
		this(TEXT_ALIGN_LEFT, Color.WHITE);
	}
	
	public TextComposition(int textAlignment, Color textColor) {
		this.textAlignment = textAlignment;
		this.textColor = textColor;
	}

	public void setTextAlignment(int alignment) {
		textAlignment = alignment;
	}
	
	public int getTextAlignment() {
		return textAlignment;
	}
	
	public void setTextColor(Color color) {
		if (color == null)
			throw new IllegalArgumentException("Color is null");

		textColor = color;
	}
	
	public Color getTextColor() {
		return textColor;
	}
}
