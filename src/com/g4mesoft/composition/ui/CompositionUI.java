package com.g4mesoft.composition.ui;

import java.awt.Color;

import com.g4mesoft.composition.Composition;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public abstract class CompositionUI {

	public abstract void bindUI(Composition composition);

	public abstract void unbindUI(Composition composition);
	
	public void layoutChanged(IRenderingContext2D context) {
	}
	
	public abstract void update();

	public abstract void render(IRenderer2D renderer, float dt);
	
	public void drawBorder(IRenderer2D renderer, Composition composition) {
		int borderFlags = composition.getBorderFlags();
		// No need to draw an empty border.
		if (borderFlags == Composition.BORDER_NONE)
			return;
		
		Color borderColor = composition.getBorderColor();
		if (borderColor == null)
			return;
		renderer.setColor(borderColor);
		
		// The border width is garenteed
		// to be greater than zero.
		int borderWidth = composition.getBorderWidth();

		int y0 = composition.getY();

		int h = composition.getHeight();
		if ((borderFlags & Composition.BORDER_TOP) != 0) {
			y0 -= borderWidth;
			h += borderWidth;
		}

		if ((borderFlags & Composition.BORDER_BOTTOM) != 0)
			h += borderWidth;
		
		if ((borderFlags & Composition.BORDER_LEFT) != 0) {
			int xp = composition.getX() - borderWidth;
			renderer.fillRect(xp, y0, borderWidth, h);
		}

		if ((borderFlags & Composition.BORDER_RIGHT) != 0) {
			int xp = composition.getX() + composition.getWidth();
			renderer.fillRect(xp, y0, borderWidth, h);
		}

		if ((borderFlags & Composition.BORDER_TOP) != 0) {
			int yp = composition.getY() - borderWidth;
			renderer.fillRect(composition.getX(), yp, composition.getWidth(), borderWidth);
		}

		if ((borderFlags & Composition.BORDER_BOTTOM) != 0) {
			int yp = composition.getY() + composition.getHeight();
			renderer.fillRect(composition.getX(), yp, composition.getWidth(), borderWidth);
		}
	}
	
	public abstract Vec2i getPreferredSize(IRenderingContext2D context);
}
