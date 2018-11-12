package com.g4mesoft.composition.ui;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import com.g4mesoft.composition.Composition;
import com.g4mesoft.composition.LabelComposition;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public class LabelCompositionUI extends CompositionUI {

	private static final String TRIMMED_TEXT_ELLIPSIS = "...";
	
	protected LabelComposition label;
	
	@Override
	public void bindUI(Composition composition) {
		if (label != null)
			throw new IllegalStateException("UI already bound!");
		
		label = (LabelComposition)composition;

		// Install defaults.
		label.setTextColor(Color.WHITE);
		label.setBackground(null);
		label.setTextAlignment(LabelComposition.TEXT_ALIGN_LEFT);
	}

	@Override
	public void unbindUI(Composition composition) {
		if (label == null)
			throw new IllegalStateException("UI not bound!");
		
		label = null;
	}

	@Override
	public void update() {
	}

	@Override
	public void render(IRenderer2D renderer, float dt) {
		Color background = label.getBackground();
		if (background != null) {
			renderer.setColor(background);
			renderer.fillRect(label.getX(), label.getY(), label.getWidth(), label.getHeight());
		}

		String text = label.getText();
		
		// We don't need to draw an
		// empty string.
		if (text != null && !text.isEmpty()) {
			text = trimText(renderer, text, label.getWidth());
			
			Rectangle2D textBounds = renderer.getStringBounds(text);
	
			int textAlignment = label.getTextAlignment();
			
			// Default alignment is:
			//     TEXT_ALIGN_LEFT
			int x = label.getX() - (int)textBounds.getX();
			if (textAlignment == LabelComposition.TEXT_ALIGN_CENTER) {
				x += (label.getWidth() - (int)textBounds.getWidth()) / 2;
			} else if (textAlignment == LabelComposition.TEXT_ALIGN_RIGHT) {
				x += label.getWidth() - (int)textBounds.getWidth();
			}
			
			// Calculate string baseline.
			int y = label.getY() - (int)textBounds.getY();
			
			// To draw the text on the center
			// of the vertical axis, we should
			// offset it as follows.
			y += (label.getHeight() - (int)textBounds.getHeight()) / 2;
			
			renderer.setColor(label.getTextColor());
			renderer.drawString(text, x, y);
		}
	}
	
	/**
	 * Trims the given text to fit within the given available 
	 * width. If the text does not fit in the available width, 
	 * it will be trimmed, and an ellipsis '...' will be added 
	 * to the end of the text.
	 * <br><br>
	 * For example. Trimming 'Hello my world!' as follows:
	 * <pre>
	 * | - avail - |
	 * |           |
	 * |Hello my world!
	 * |           |
	 * </pre>
	 * would result in 'Hello my...'
	 * <pre>
	 * | - avail - |
	 * |           |
	 * |Hello my...|
	 * |           |
	 * </pre>
	 * Trimming an empty string will result in an empty string. If
	 * the given text does not allow for any characters within the
	 * available width, the default ellipsis '...' will be returned.
	 * 
	 * @param renderer - The currently rendering context.
	 * @param text - The text to be trimmed.
	 * @param availableWidth - The specified available width.
	 * 
	 * @return A trimmed version of the text, which can be drawn within
	 *         the specified availableWidth.
	 */
	protected String trimText(IRenderer2D renderer, String text, int availableWidth) {
		int len = text.length();
		if (len <= 0)
			return text;

		// Text fits inside bounds.
		if (renderer.getStringWidth(text) <= availableWidth)
			return text;
		
		availableWidth -= renderer.getStringWidth(TRIMMED_TEXT_ELLIPSIS);
		
		// No space for any other
		// characters. 
		if (availableWidth < 0)
			return TRIMMED_TEXT_ELLIPSIS;

		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			// Should probably use getStringWidth
			// and substring instead, but for 
			// optimization we use getCharWidth.
			availableWidth -= renderer.getCharWidth(c);
			
			if (availableWidth < 0)
				return text.substring(0, i) + TRIMMED_TEXT_ELLIPSIS;
		}
		
		// This should never happen.
		return text;
	}

	@Override
	public Vec2i getPreferredSize(IRenderingContext2D context) {
		Vec2i preferredSize = new Vec2i(0, 0);

		String text = label.getText();
		if (text.isEmpty())
			return preferredSize;
		
		Rectangle2D textBounds = context.getStringBounds(text);
		return preferredSize.set((int)textBounds.getWidth(), (int)textBounds.getHeight());
	}

}
