package com.g4mesoft.composition.ui;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import com.g4mesoft.Application;
import com.g4mesoft.composition.Composition;
import com.g4mesoft.composition.text.TextComposition;
import com.g4mesoft.composition.text.editable.BasicTextCaret;
import com.g4mesoft.composition.text.editable.ITextCaret;
import com.g4mesoft.composition.text.editable.ITextModel;
import com.g4mesoft.composition.text.editable.TextFieldComposition;
import com.g4mesoft.graphic.GColor;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.input.key.KeyTypedInput;
import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec2i;

public class TextFieldCompositionUI extends EditableTextCompositionUI {

	private static final int CARET_BLINK_RATE = 500;
	private static final int CARET_WIDTH = 2;
	private static final int CARET_INSETS = 0;
	
	private static final int PRINTABLE_CHARACTERS_START = 0x20;
	private static final int DELETE_CONTROL_CHARACTER = 0x7F;
	
	private static final int BACKSPACE_CONTROL_CHARACTER = 0x08;

	private static final GColor SELECTION_BACKGROUND_COLOR = new GColor(14, 108, 220);
	
	private TextFieldComposition textField;
	private ITextCaret caret;
	
	private KeyTypedInput typedInput;
	
	private String clippedText;
	private int clippedModelStart;
	private int clippedModelEnd;
	private int clippedViewOffset;
	
	private int oldCaretLocation;
	private int oldCaretPointX;
	
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
		textField.setSelectionBackgroundColor(SELECTION_BACKGROUND_COLOR);
		textField.setSelectionTextColor(GColor.WHITE);
		
		typedInput = new KeyTypedInput();
		Application.addTypedKey(typedInput);
		
		if (textField.getCaret() == null) {
			BasicTextCaret caret = new BasicTextCaret();
			textField.setCaret(caret);
			this.caret = caret;
			
			caret.setCaretWidth(CARET_WIDTH);
			caret.setCaretInsets(CARET_INSETS);
			caret.setBlinkRate(CARET_BLINK_RATE);
		}
	}

	@Override
	public void unbindUI(Composition composition) {
		if (textField == null)
			throw new IllegalStateException("UI not bound!");
		
		// Remove typed input to release 
		// resources. 
		Application.removeTypedKey(typedInput);
		typedInput = null;
		
		if (caret == textField.getCaret())
			textField.setCaret(null);
		caret = null;

		clippedText = null;
		clippedModelStart = clippedModelEnd = 0;
		clippedViewOffset = 0;
		
		textField = null;
	}

	@Override
	public void layoutChanged(IRenderingContext2D context) {
		reconstructClippedModel(context);
	}
	
	private int expandClippedModelLeft(IRenderingContext2D context, int availableWidth) {
		ITextModel model = textField.getTextModel();
		while (availableWidth > 0 && clippedModelStart > 0) {
			clippedModelStart--;
			
			char c = model.getChar(clippedModelStart);
			availableWidth -= context.getCharWidth(c);
		}
		
		return availableWidth;
	}

	private int expandClippedModelRight(IRenderingContext2D context, int availableWidth) {
		ITextModel model = textField.getTextModel();
		while (availableWidth > 0 && clippedModelEnd < model.getLength()) {
			char c = model.getChar(clippedModelEnd);
			availableWidth -= context.getCharWidth(c);
			
			clippedModelEnd++;
		}
		
		return availableWidth;
	}
	
	private void reconstructClippedModel(IRenderingContext2D context) {
		ITextModel model = textField.getTextModel();
		int caretLocation = MathUtils.clamp(getCaretLocation(), 0, model.getLength());

		int caretX;
		if (caretLocation <= clippedModelStart) {
			caretX = 0;
		} else if (caretLocation >= clippedModelEnd) {
			caretX = textField.getWidth();
		} else {
			caretX = MathUtils.clamp(oldCaretPointX, 0, textField.getWidth());
		}
		
		clippedModelStart = caretLocation;
		clippedViewOffset = expandClippedModelLeft(context, caretX);
		
		if (clippedViewOffset > 0) {
			caretX -= clippedViewOffset;
			clippedViewOffset = 0;
		}
		
		int width = textField.getWidth();
		clippedModelEnd = caretLocation;

		int availableWidth = expandClippedModelRight(context, width - caretX);
		if (availableWidth > 0 && clippedModelStart > 0) {
			availableWidth += clippedViewOffset;
			clippedViewOffset = expandClippedModelLeft(context, availableWidth);

			availableWidth = 0;
		}
		
		// Fix alignments
		availableWidth += clippedViewOffset;
		if (availableWidth > 0) {
			switch (textField.getTextAlignment()) {
			case TextComposition.TEXT_ALIGN_RIGHT:
				clippedViewOffset = availableWidth;
				break;
			case TextComposition.TEXT_ALIGN_CENTER:
				clippedViewOffset = availableWidth / 2;
				break;
			case TextComposition.TEXT_ALIGN_LEFT:
			default:
				clippedViewOffset = 0;
			}
		}
		
		int count = clippedModelEnd - clippedModelStart;
		clippedText = (count != 0) ? model.getText(clippedModelStart, count) : "";
	}
	
	@Override
	public void update() {
		if (textField.isEditable()) {
			if (typedInput.hasTypedCharacters())
				handleTypedCharacters(typedInput.flushBuffer());
			typedInput.recordNextUpdate();
	
			ITextCaret caret = textField.getCaret();
			if (caret != null)
				caret.update();
		}
	}
	
	protected void handleTypedCharacters(char[] typedChars) {
		if (typedChars == null || typedChars.length <= 0)
			return;
		
		for (int j = 0; j < typedChars.length; j++) {
			char c = typedChars[j];
			
			if (isTypeableCharacter(c)) {
				ITextCaret caret = textField.getCaret();

				boolean hasCaretSelection = false;
				if (caret != null && caret.hasCaretSelection()) {
					hasCaretSelection = true;

					int selectStart = getCaretSelectionStart();
					int selectEnd = getCaretSelectionEnd();
					textField.getTextModel().removeText(selectStart, selectEnd - selectStart);
				}
				
				if (!hasCaretSelection || !isControlCharacter(c))
					insertTypedChar(getCaretLocation(), c);
			}
		}
	}
	
	private boolean isTypeableCharacter(char c) {
		if (!isControlCharacter(c))
			return true;
		
		return c == BACKSPACE_CONTROL_CHARACTER ||
		       c == DELETE_CONTROL_CHARACTER;
	}
	
	private void insertTypedChar(int offset, char c) {
		ITextModel model = textField.getTextModel();
		
		if (isControlCharacter(c)) {
			switch (c) {
			case BACKSPACE_CONTROL_CHARACTER:
				if (offset > 0)
					model.removeText(offset - 1, 1);
				break;
			case DELETE_CONTROL_CHARACTER:
				if (offset < model.getLength())
					model.removeText(offset, 1);
				break;
			}
		} else {
			model.insertChar(offset, c);
		}
	}
	
	private int getCaretLocation() {
		ITextCaret caret = textField.getCaret();
		if (caret == null)
			return textField.getTextModel().getLength();
		return caret.getCaretLocation();
	}
	
	private int getCaretSelectionStart() {
		ITextCaret caret = textField.getCaret();
		if (caret == null)
			return -1;
		return MathUtils.min(caret.getCaretDot(), caret.getCaretMark());
	}

	private int getCaretSelectionEnd() {
		ITextCaret caret = textField.getCaret();
		if (caret == null)
			return -1;
		return MathUtils.max(caret.getCaretDot(), caret.getCaretMark());
	}
	
	private boolean isControlCharacter(char c) {
		return c < PRINTABLE_CHARACTERS_START || c == DELETE_CONTROL_CHARACTER;
	}

	private boolean isLocationInView(IRenderingContext2D context, int caretLocation) {
		if (caretLocation > clippedModelStart && caretLocation < clippedModelEnd)
			return true;
		
		if (caretLocation == clippedModelStart) {
			return clippedViewOffset >= 0;
		} else if (caretLocation == clippedModelEnd) {
			int clipWidth = context.getStringWidth(clippedText);
			return clipWidth + clippedViewOffset <= textField.getWidth();
		}
		
		return false;
	}
	
	private void checkViewChange(IRenderingContext2D context) {
		int caretLocation = getCaretLocation();
		if (!isLocationInView(context, caretLocation))
			reconstructClippedModel(context);
		
		if (caretLocation != oldCaretLocation) {
			Rectangle caretBounds = modelToView(context, caretLocation);
			if (caretBounds != null) {
				oldCaretPointX = caretBounds.x - textField.getX();
			} else {
				int mid = (clippedModelStart + clippedModelEnd) / 2;
				oldCaretPointX = (caretLocation < mid) ? 0 : textField.getWidth();
			}
			
			oldCaretLocation = caretLocation;
		}
	}
	
	@Override
	public void render(IRenderer2D renderer, float dt) {
		checkViewChange(renderer);
		
		drawBackground(renderer, textField, textField.getBackground());

		int selectStart = getCaretSelectionStart();
		int selectEnd = getCaretSelectionEnd();
		
		ITextCaret caret = textField.getCaret();
		boolean hasSelection = (caret != null && caret.hasCaretSelection());
		
		// Only draw text if it is not all selected.
		if (!hasSelection || selectStart > clippedModelStart || selectEnd < clippedModelEnd) {
			renderer.setColor(textField.getTextColor());
			if (hasSelection) {
				drawVisibleTextSegment(renderer, clippedModelStart, selectStart);
				drawVisibleTextSegment(renderer, selectEnd, clippedModelEnd);
			} else {
				drawVisibleTextSegment(renderer, clippedModelStart, clippedModelEnd);
			}
		}
		
		if (hasSelection && selectEnd > clippedModelStart && selectStart < clippedModelEnd)
			drawCaretSelection(renderer, selectStart, selectEnd);
		
		drawBorder(renderer, textField);

		if (textField.isEditable() && caret != null)
			caret.render(renderer, dt);
	}
	
	protected void drawVisibleTextSegment(IRenderer2D renderer, int modelStart, int modelEnd) {
		int clipOffset = modelStart - clippedModelStart;
		if (clipOffset < 0)
			clipOffset = 0;
		
		int clipLength = modelEnd - modelStart;
		if (clipLength > clippedText.length() - clipOffset)
			clipLength = clippedText.length() - clipOffset;

		// No need to render an empty string.
		if (clipOffset >= clippedText.length() || clipLength <= 0)
			return;
		
		int x = textField.getX() + clippedViewOffset;
		int y = textField.getY();
		
		String text = clippedText;
		Rectangle2D textBounds;
		
		if (clipLength != clippedText.length()) {
			text = clippedText.substring(clipOffset, clipOffset + clipLength);
			
			if (clipOffset != 0)
				x += renderer.getStringWidth(clippedText.substring(0, clipOffset));
		}
		
		textBounds = renderer.getStringBounds(text);
		x -= (int)textBounds.getX();
		y -= (int)textBounds.getY();
		
		renderer.drawString(text, x, y);
	}
	
	protected void drawCaretSelection(IRenderer2D renderer, int selectStart, int selectEnd) {
		int x0, x1;
		if (selectStart >= clippedModelStart) {
			Rectangle sBounds = modelToView(renderer, selectStart);
			x0 = MathUtils.max(textField.getX(), sBounds.x);
		} else {
			x0 = textField.getX();
		}
		
		if (selectEnd <= clippedModelEnd) {
			Rectangle eBounds = modelToView(renderer, selectEnd);
			x1 = MathUtils.min(textField.getX() + textField.getWidth(), eBounds.x);
		} else {
			x1 = textField.getX() + textField.getWidth();
		}
		
		if (x0 < textField.getX() + textField.getWidth() && x1 >= textField.getX()) {
			GColor selectionBackgroundColor = textField.getSelectionBackgroundColor();
			if (selectionBackgroundColor == null)
				selectionBackgroundColor = textField.getBackground().invert(0xFF);
	
			renderer.setColor(selectionBackgroundColor);
			renderer.fillRect(x0, textField.getY(), x1 - x0, textField.getHeight());
	
			GColor selectionTextColor = textField.getSelectionTextColor();
			if (selectionTextColor == null)
				selectionTextColor = selectionBackgroundColor.invert(0xFF);
			
			renderer.setColor(selectionTextColor);
			drawVisibleTextSegment(renderer, selectStart, selectEnd);
		}
	}

	@Override
	public Vec2i getPreferredSize(IRenderingContext2D context) {
		if (textField.getTextModel().getLength() == 0)
			return new Vec2i(0, context.getFontHeight());
		return getPreferredSize(context, textField.getText());
	}

	@Override
	public Rectangle modelToView(IRenderingContext2D context, int location) {
		// Make sure we're within view
		if (location < clippedModelStart || location > clippedModelEnd)
			return null;
		

		Rectangle bounds = new Rectangle();
		bounds.x = textField.getX() + clippedViewOffset;
		bounds.y = textField.getY();
		
		if (clippedText.isEmpty()) {
			bounds.width = 0;
			bounds.height = textField.getPreferredSize(context).y;
			return bounds;
		}

		int offset = location - clippedModelStart;
		bounds.x += context.getStringWidth(clippedText.substring(0, offset));
		
		char c;
		if (offset == clippedText.length()) {
			c = clippedText.charAt(offset - 1);
			bounds.width = 0;
		} else {
			c = clippedText.charAt(offset);
			bounds.width = context.getCharWidth(c);
		}
		
		// We may be dealing with special characters
		// with a different height than the usual font
		// height. Get the string bounds for that.
		Rectangle2D charBounds = context.getStringBounds(Character.toString(c));
		bounds.height = (int)MathUtils.ceil(charBounds.getHeight());
		
		return bounds;
	}

	@Override
	public int viewToModel(IRenderingContext2D context, int x, int y) {
		if (!textField.isInBounds(x, y))
			return -1;

		int baseDist = x - clippedViewOffset - textField.getX();
		int minimumDist = MathUtils.abs(baseDist);

		int index = 0;
		while (index < clippedText.length()) {
			String text = clippedText.substring(0, index + 1);
			int width = context.getStringWidth(text);
			int dist = MathUtils.abs(baseDist - width);
		
			if (dist > minimumDist)
				break;
				
			minimumDist = dist;
			index++;
		}
		
		return index + clippedModelStart;
	}
}
