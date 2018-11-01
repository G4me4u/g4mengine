package com.g4mesoft.composition;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public class LabelComposition extends Composition {

	public static final int TEXT_ALIGN_CENTER = 0;
	public static final int TEXT_ALIGN_LEFT = 1;
	public static final int TEXT_ALIGN_RIGHT = 2;
	
	protected String text;
	private Color textColor;
	
	private int textAlignment;

	public LabelComposition() {
		this(null);
	}
	
	public LabelComposition(String text) {
		this.text = (text != null ? text : "");
		
		textColor = Color.WHITE;
		textAlignment = TEXT_ALIGN_LEFT;
	}

	@Override
	public void render(IRenderer2D renderer, float dt) {
		renderer.setColor(textColor);
		
		Rectangle2D textBounds = renderer.getStringBounds(text);

		// Default alignment is:
		//     TEXT_ALIGN_LEFT
		int x = getX() - (int)textBounds.getX();
		if (textAlignment == TEXT_ALIGN_CENTER) {
			x += (getWidth() - (int)textBounds.getWidth()) / 2;
		} else if (textAlignment == TEXT_ALIGN_RIGHT) {
			x += getWidth() - (int)textBounds.getWidth();
		}
		
		// Calculate string baseline.
		int y = getY() - (int)textBounds.getY();
		
		// To draw the text on the center
		// of the vertical axis, we should
		// offset it as follows.
		y += (getHeight() - (int)textBounds.getHeight()) / 2;
		
		renderer.drawString(text, x, y);
	}
	
	@Override
	protected void calculatePreferredSize(Vec2i preferredSize, IRenderingContext2D context) {
		Rectangle2D textBounds = context.getStringBounds(text);
		preferredSize.set((int)textBounds.getWidth(), (int)textBounds.getHeight());
	}
	
	public void setTextColor(Color color) {
		if (color == null)
			throw new NullPointerException("Color is null");
		textColor = color;
	}
	
	public void setText(String text) {
		if (text == null)
			text = "";
		this.text = text;
		invalidate();
	}
	
	public String getText() {
		return text;
	}
	
	public void setTextAlignment(int alignment) {
		textAlignment = alignment;
	}
}
