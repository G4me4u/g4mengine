package com.g4mesoft.composition.ui;

import java.awt.Color;

import com.g4mesoft.composition.Composition;
import com.g4mesoft.composition.LayoutComposition;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public class LayoutCompositionUI extends CompositionUI {

	private LayoutComposition layout;
	
	@Override
	public void bindUI(Composition composition) {
		if (layout != null)
			throw new IllegalStateException("UI already bound!");
		
		layout = (LayoutComposition)composition;
		
	}

	@Override
	public void unbindUI(Composition composition) {
		if (layout == null)
			throw new IllegalStateException("UI not bound!");
		
		layout = null;
	}

	@Override
	public void update() {
	}

	@Override
	public void render(IRenderer2D renderer, float dt) {
		Color background = layout.getBackground();
		if (background != null) {
			renderer.setColor(background);
			renderer.fillRect(layout.getX(), layout.getY(), layout.getWidth(), layout.getHeight());
		}
	}

	@Override
	public Vec2i getPreferredSize(IRenderingContext2D context) {
		return null;
	}
}
