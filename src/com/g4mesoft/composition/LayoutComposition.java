package com.g4mesoft.composition;

import java.util.LinkedList;

import com.g4mesoft.composition.ui.LayoutCompositionUI;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public abstract class LayoutComposition extends Composition {

	protected final LinkedList<Composition> children;
	
	public LayoutComposition() {
		children = new LinkedList<Composition>();
		
		setUI(new LayoutCompositionUI());
	}

	public void addComposition(Composition composition) {
		if (composition == null)
			throw new NullPointerException("Composition is null!");
		if (composition == this)
			throw new IllegalArgumentException("Can't add composition to itself!");
		if (composition.getParent() != null)
			throw new IllegalArgumentException("Composition already has a parent!");
		
		children.add(composition);
		composition.setParent(this);
		
		invalidate();
	}
	
	public Composition removeComposition(Composition composition) {
		if (children.remove(composition)) {
			invalidate();
			return composition;
		}
		return null;
	}
	
	@Override
	public Composition getCompositionAt(int x, int y) {
		for (Composition comp : children) {
			int x0 = comp.getX();
			int y0 = comp.getY();
			int x1 = x0 + comp.getWidth();
			int y1 = y0 + comp.getHeight();

			// Test if point is in bounds 
			// of child composition.
			if (x >= x0 && x < x1 && y >= y0 && y < y1)
				return comp.getCompositionAt(x, y);
		}
		
		return super.getCompositionAt(x, y);
	}
	
	@Override
	public void update() {
		super.update();
		
		for (Composition child : children)
			child.update();
	}
	
	@Override
	public void render(IRenderer2D renderer, float dt) {
		super.render(renderer, dt);
		
		for (Composition comp : children)
			comp.render(renderer, dt);
	}
	
	protected abstract void calculatePreferredSize(Vec2i preferredSize, IRenderingContext2D context);
	
}
