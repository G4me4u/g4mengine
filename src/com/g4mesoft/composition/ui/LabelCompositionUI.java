package com.g4mesoft.composition.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import com.g4mesoft.composition.Composition;
import com.g4mesoft.composition.text.LabelComposition;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public class LabelCompositionUI extends TextCompositionUI {

	protected Rectangle labelBounds;
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
		
		labelBounds = new Rectangle();
	}

	@Override
	public void unbindUI(Composition composition) {
		if (label == null)
			throw new IllegalStateException("UI not bound!");
		
		labelBounds = null;
		
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
			// Trim and draw text.
			text = trimText(renderer, text, label.getWidth());
			
			labelBounds.setLocation(label.getX(), label.getY());
			labelBounds.setSize(label.getWidth(), label.getHeight());
			drawAlignedText(renderer, text, label, labelBounds);
		}
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
