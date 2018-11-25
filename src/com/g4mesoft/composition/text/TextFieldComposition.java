package com.g4mesoft.composition.text;

import com.g4mesoft.composition.ui.TextFieldCompositionUI;

public class TextFieldComposition extends TextComposition {

	public static final int TEXT_ALIGN_CENTER = LabelComposition.TEXT_ALIGN_CENTER;
	public static final int TEXT_ALIGN_LEFT = LabelComposition.TEXT_ALIGN_LEFT;
	public static final int TEXT_ALIGN_RIGHT = LabelComposition.TEXT_ALIGN_RIGHT;
	
	private String text;
	
	private boolean editable;
	
	public TextFieldComposition() {
		this(null);
	}
	
	public TextFieldComposition(String text) {
		this.text = text != null ? text : "";
	
		editable = true;
		
		// Set UI
		setUI(new TextFieldCompositionUI());
	}
	
	public void setText(String text) {
		if (text == null)
			text = "";
		this.text = text;
		
		requestRelayout(true);
	}
	
	public void appendText(String text) {
		if (text == null)
			return;
		this.text += text;
		
		requestRelayout(true);
	}
	
	public String getText() {
		return text;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public boolean isEditable() {
		return editable;
	}
}
