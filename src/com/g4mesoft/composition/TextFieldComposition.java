package com.g4mesoft.composition;

import java.awt.Color;

import com.g4mesoft.composition.ui.TextFieldCompositionUI;

public class TextFieldComposition extends Composition {

	public static final int TEXT_ALIGN_CENTER = LabelComposition.TEXT_ALIGN_CENTER;
	public static final int TEXT_ALIGN_LEFT = LabelComposition.TEXT_ALIGN_LEFT;
	public static final int TEXT_ALIGN_RIGHT = LabelComposition.TEXT_ALIGN_RIGHT;
	
	private String text;
	private Color textColor;

	private int textAlignment;
	private boolean editable;
	
	public TextFieldComposition() {
		this(null);
	}
	
	public TextFieldComposition(String text) {
		this.text = text != null ? text : "";
	
		textColor = Color.WHITE;
		textAlignment = TEXT_ALIGN_LEFT;
	
		editable = true;
		
		// Set UI
		setUI(new TextFieldCompositionUI());
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
	
	public void appendText(String text) {
		if (text == null)
			return;
		this.text += text;
		
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
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public boolean isEditable() {
		return editable;
	}
}
