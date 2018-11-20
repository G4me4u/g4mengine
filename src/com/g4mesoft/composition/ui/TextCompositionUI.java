package com.g4mesoft.composition.ui;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import com.g4mesoft.composition.text.LabelComposition;
import com.g4mesoft.composition.text.TextComposition;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public abstract class TextCompositionUI extends CompositionUI {

	private static final String TRIMMED_TEXT_ELLIPSIS = "...";
	
	public void drawAlignedText(IRenderer2D renderer, String text, TextComposition comp, Rectangle bounds) {
		// No need to draw an empty string
		if (text == null || text.isEmpty())
			return;
		
		Rectangle2D textBounds = renderer.getStringBounds(text);
		
		int textAlignment = comp.getTextAlignment();
		
		// Default alignment is:
		//     TEXT_ALIGN_LEFT
		int x = bounds.x - (int)textBounds.getX();
		if (textAlignment == LabelComposition.TEXT_ALIGN_CENTER) {
			x += (bounds.width - (int)textBounds.getWidth()) / 2;
		} else if (textAlignment == LabelComposition.TEXT_ALIGN_RIGHT) {
			x += bounds.width - (int)textBounds.getWidth();
		}
		
		// Calculate string baseline.
		int y = bounds.y - (int)textBounds.getY();
		
		// To draw the text on the center
		// of the vertical axis, we should
		// offset it as follows.
		y += (bounds.height - (int)textBounds.getHeight()) / 2;
		
		renderer.setColor(comp.getTextColor());
		renderer.drawString(text, x, y);
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
	 * @param context - The current rendering-context.
	 * @param text - The text to be trimmed.
	 * @param availableWidth - The specified available width.
	 * 
	 * @return A trimmed version of the text, which can be drawn within
	 *         the specified availableWidth.
	 */
	protected String trimText(IRenderingContext2D context, String text, int availableWidth) {
		int len = text.length();
		if (len <= 0)
			return text;

		// Text fits inside bounds.
		if (context.getStringWidth(text) <= availableWidth)
			return text;
		
		availableWidth -= context.getStringWidth(TRIMMED_TEXT_ELLIPSIS);
		
		// No space for any other
		// characters. 
		if (availableWidth < 0)
			return TRIMMED_TEXT_ELLIPSIS;

		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			// Should probably use getStringWidth
			// and substring instead, but for 
			// optimization we use getCharWidth.
			availableWidth -= context.getCharWidth(c);
			
			if (availableWidth < 0)
				return text.substring(0, i) + TRIMMED_TEXT_ELLIPSIS;
		}
		
		// This should never happen.
		return text;
	}
	
	public Vec2i getPreferredSize(IRenderingContext2D context, String text) {
		Vec2i preferredSize = new Vec2i(0, 0);

		if (text.isEmpty())
			return preferredSize;
		
		Rectangle2D textBounds = context.getStringBounds(text);
		return preferredSize.set((int)textBounds.getWidth(), (int)textBounds.getHeight());
	}
}
