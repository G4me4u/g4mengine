package com.g4mesoft.composition.text;

import com.g4mesoft.composition.ui.LabelCompositionUI;

public class LabelComposition extends TextComposition {

	protected String text;
	
	public LabelComposition() {
		this(null);
	}
	
	public LabelComposition(String text) {
		this.text = (text != null ? text : "");
		
		// Set UI
		setUI(new LabelCompositionUI());
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
}
