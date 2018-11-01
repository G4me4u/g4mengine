package com.g4mesoft.composition.ui;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import com.g4mesoft.composition.Composition;
import com.g4mesoft.composition.LabelComposition;
import com.g4mesoft.composition.TextFieldComposition;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.input.key.KeyInputListener;
import com.g4mesoft.input.key.KeyTypedInput;
import com.g4mesoft.math.Vec2i;

public class TextFieldCompositionUI extends CompositionUI {

	private static final int PRINTABLE_CHARACTERS_START = 0x20;
	private static final int DELETE_CONTROL_CHARACTER = 0x7F;
	
	private static final int BACKSPACE_CONTROL_CHARACTER = 0x08;
	
	private TextFieldComposition textField;
	
	private KeyTypedInput typedInput;
	
	@Override
	public void bindUI(Composition composition) {
		if (textField != null)
			throw new IllegalStateException("UI already bound!");
		
		textField = (TextFieldComposition)composition;
	
		// Install defaults
		textField.setTextColor(Color.WHITE);
		textField.setTextAlignment(TextFieldComposition.TEXT_ALIGN_LEFT);

		typedInput = new KeyTypedInput();
		KeyInputListener.getInstance().addTypedKey(typedInput);
	}

	@Override
	public void unbindUI(Composition composition) {
		if (textField == null)
			throw new IllegalStateException("UI not bound!");

		// Remove typed input to release 
		// resources. 
		KeyInputListener.getInstance().removeTypedKey(typedInput);
		typedInput = null;

		textField = null;
	}

	@Override
	public void update() {
		if (typedInput.hasTypedCharacters())
			handleTypedCharacters(typedInput.flushBuffer());
		typedInput.recordNextUpdate();
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
		String text = textField.getText();
		
		if (text == null || text.isEmpty())
			return;
		
		Rectangle2D textBounds = renderer.getStringBounds(text);

		int textAlignment;
		if (textBounds.getWidth() > textField.getWidth()) {
			textAlignment = TextFieldComposition.TEXT_ALIGN_RIGHT;
		} else {
			textAlignment = textField.getTextAlignment();
		}
		
		int x = textField.getX() - (int)textBounds.getX();
		if (textAlignment == LabelComposition.TEXT_ALIGN_CENTER) {
			x += (textField.getWidth() - (int)textBounds.getWidth()) / 2;
		} else if (textAlignment == LabelComposition.TEXT_ALIGN_RIGHT) {
			x += textField.getWidth() - (int)textBounds.getWidth();
		}
		
		int y = textField.getY() - (int)textBounds.getY();
		y += (textField.getHeight() - (int)textBounds.getHeight()) / 2;
		
		renderer.setColor(textField.getTextColor());
		renderer.drawString(text, x, y);
	}

	@Override
	public Vec2i getPreferredSize(IRenderingContext2D context) {
		Vec2i preferredSize = new Vec2i(0, 0);

		String text = textField.getText();
		if (text.isEmpty())
			return preferredSize;
		
		Rectangle2D textBounds = context.getStringBounds(text);
		return preferredSize.set((int)textBounds.getWidth(), (int)textBounds.getHeight());
	}
}
