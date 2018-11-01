package com.g4mesoft.composition.ui;

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
		label = (LabelComposition)composition;
	}

	@Override
	public void unbindUI(Composition composition) {
	}

	@Override
	public void update() {
	}

	@Override
	public void render(IRenderer2D renderer, float dt) {
		String text = label.getText();
		
		// We don't need to draw an
		// empty string.
		if (text == null || text.isEmpty())
			return;
		
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
