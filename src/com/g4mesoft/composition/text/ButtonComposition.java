package com.g4mesoft.composition.text;

import com.g4mesoft.composition.ui.ButtonCompositionUI;

public class ButtonComposition extends TextComposition {

	private String text;
	
	public ButtonComposition() {
		this(null);
	}

	public ButtonComposition(String text) {
		this.text = text == null ? "" : text;

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
}
