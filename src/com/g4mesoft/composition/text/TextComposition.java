package com.g4mesoft.composition.text;

import com.g4mesoft.composition.Composition;
import com.g4mesoft.graphic.GColor;

public abstract class TextComposition extends Composition {

	public static final int TEXT_ALIGN_CENTER = 0;
	public static final int TEXT_ALIGN_LEFT = 1;
	public static final int TEXT_ALIGN_RIGHT = 2;
	
	protected int textAlignment;
	protected GColor textColor;
	
	public TextComposition() {
		this(TEXT_ALIGN_LEFT, GColor.WHITE);
	}
	
	public TextComposition(int textAlignment, GColor textColor) {
		this.textAlignment = textAlignment;
		this.textColor = textColor;
	}

	public void setTextAlignment(int alignment) {
		textAlignment = alignment;
	}
	
	public int getTextAlignment() {
		return textAlignment;
	}
	
	public void setTextColor(GColor color) {
		if (color == null)
			throw new IllegalArgumentException("Color is null");

		textColor = color;
	}
	
	public GColor getTextColor() {
		return textColor;
	}
}
