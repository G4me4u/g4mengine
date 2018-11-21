package com.g4mesoft.composition.ui;

import java.awt.Color;
import java.awt.Rectangle;

import com.g4mesoft.composition.Composition;
import com.g4mesoft.composition.text.LabelComposition;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public class LabelCompositionUI extends TextCompositionUI {

	protected LabelComposition label;

	protected String trimmedText;
	protected Rectangle labelBounds;
	
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
		trimmedText = null;
		
		label = null;
	}

	@Override
	public void layoutChanged(IRenderingContext2D context) {
		String text = label.getText();
		
		// We don't need to trim an
		// empty string!
		if (text != null && !text.isEmpty()) {
			// Trim text
			trimmedText = trimText(context, text, label.getWidth());
		} else {
			trimmedText = null;
		}
		
		labelBounds.setLocation(label.getX(), label.getY());
		labelBounds.setSize(label.getWidth(), label.getHeight());
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
		
		if (trimmedText != null) {
			renderer.setColor(label.getTextColor());
			drawAlignedText(renderer, trimmedText, label.getTextAlignment(), labelBounds);
		}

		drawBorder(renderer, label);
	}
	
	@Override
	public Vec2i getPreferredSize(IRenderingContext2D context) {
		return getPreferredSize(context, label.getText());
	}
}
