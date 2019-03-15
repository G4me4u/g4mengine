package com.g4mesoft.composition.ui;

import java.awt.Rectangle;

import com.g4mesoft.Application;
import com.g4mesoft.composition.Composition;
import com.g4mesoft.composition.text.TextFieldComposition;
import com.g4mesoft.graphic.GColor;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.input.key.KeyTypedInput;
import com.g4mesoft.math.Vec2i;

public class TextFieldCompositionUI extends TextCompositionUI {

	private static final int PRINTABLE_CHARACTERS_START = 0x20;
	private static final int DELETE_CONTROL_CHARACTER = 0x7F;
	
	private static final int BACKSPACE_CONTROL_CHARACTER = 0x08;
	
	private TextFieldComposition textField;
	
	private KeyTypedInput typedInput;
	private Rectangle fieldBounds;
	
	@Override
	public void bindUI(Composition composition) {
		if (textField != null)
			throw new IllegalStateException("UI already bound!");
		
		textField = (TextFieldComposition)composition;
	
		// Install defaults
		textField.setTextColor(GColor.WHITE);
		textField.setBackground(GColor.BLACK);
		textField.setTextAlignment(TextFieldComposition.TEXT_ALIGN_LEFT);

		textField.setBorderWidth(1);
		textField.setBorder(Composition.BORDER_ALL);
		textField.setBorderColor(GColor.WHITE);
		
		typedInput = new KeyTypedInput();
		Application.addTypedKey(typedInput);
		
		fieldBounds = new Rectangle();
	}

	@Override
	public void unbindUI(Composition composition) {
		if (textField == null)
			throw new IllegalStateException("UI not bound!");

		fieldBounds = null;
		
		// Remove typed input to release 
		// resources. 
		Application.removeTypedKey(typedInput);
		typedInput = null;

		textField = null;
	}

	@Override
	public void update() {
		if (textField.isEditable()) {
			if (typedInput.hasTypedCharacters())
				handleTypedCharacters(typedInput.flushBuffer());
			typedInput.recordNextUpdate();
		}
	}
	
	protected void handleTypedCharacters(char[] typedChars) {
		if (typedChars == null || typedChars.length <= 0)
			return;
		
		String currentText = textField.getText();
		
		int i = currentText.length();
		char[] newText = new char[i + typedChars.length];
		currentText.getChars(0, i, newText, 0);
		
		for (int j = 0; j < typedChars.length; j++) {
			char c = typedChars[j];
			if (isControlCharacter(c)) {
				if (c == BACKSPACE_CONTROL_CHARACTER && i > 0)
					i--;
				continue;
			}

			newText[i++] = c;
		}
		
		textField.setText(new String(newText, 0, i));
	}
	
	private boolean isControlCharacter(char c) {
		return c < PRINTABLE_CHARACTERS_START || c == DELETE_CONTROL_CHARACTER;
	}

	@Override
	public void render(IRenderer2D renderer, float dt) {
		int x = textField.getX();
		int y = textField.getY();
		
		int w = textField.getWidth();
		int h = textField.getHeight();
		
		GColor background = textField.getBackground();
		if (background != null) {
			renderer.setColor(background);
			renderer.fillRect(x, y, w, h);
		}
		
		String text = textField.getText();
		if (text != null && !text.isEmpty()) {
			fieldBounds.setBounds(x, y, w, h);
			
			int textAlignment = textField.getTextAlignment();
			int textWidth = renderer.getStringWidth(text);
			if (textWidth >= w)
				textAlignment = TextFieldComposition.TEXT_ALIGN_RIGHT;
			
			renderer.setColor(textField.getTextColor());
			drawAlignedText(renderer, text, textAlignment, fieldBounds);
		}

		drawBorder(renderer, textField);
	}

	@Override
	public Vec2i getPreferredSize(IRenderingContext2D context) {
		if (textField.getText().isEmpty())
			return new Vec2i(0, context.getFontHeight());
		return getPreferredSize(context, textField.getText());
	}
}
