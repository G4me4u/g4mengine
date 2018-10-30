package com.g4mesoft.graphic.ui;

import com.g4mesoft.input.key.KeyInputListener;
import com.g4mesoft.input.key.KeyTypedInput;

public class TextFieldComposition extends LabelComposition {

	private static KeyTypedInput defaultTypedInput = null;
	
	private final KeyTypedInput typedInput;
	
	public TextFieldComposition() {
		this(null, null);
	}
	
	public TextFieldComposition(String text) {
		this(text, null);
	}
	
	public TextFieldComposition(String text, KeyTypedInput typedInput) {
		super(text);
		
		if (typedInput == null) {
			if (defaultTypedInput == null) {
				defaultTypedInput = new KeyTypedInput();
				KeyInputListener.getInstance().addTypedKey(defaultTypedInput);
			}
			typedInput = defaultTypedInput;
		}

		this.typedInput = typedInput;
	}
	
	@Override
	public void update() {
		super.update();
	
		char[] typedChars = typedInput.getTypedChars();
		if (typedChars.length > 0) {
			String text = getText();
			StringBuilder sb = new StringBuilder(text.length() + typedChars.length);
			sb.append(text);
			sb.append(typedChars);
			setText(sb.toString());
		}
	}
}
