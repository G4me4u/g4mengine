package com.g4mesoft.composition;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.g4mesoft.composition.ui.LayoutCompositionUI;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public abstract class LayoutComposition extends Composition {

	private final LinkedList<Composition> children;
	
	public LayoutComposition() {
		children = new LinkedList<Composition>();
		
		setUI(new LayoutCompositionUI());
	}
	
	@Override
	public LayoutCompositionUI getUI() {
		return (LayoutCompositionUI)super.getUI();
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
	
	/**
	 * @return The number of children currently added to this
	 *         layout composition.
	 */
	public int getNumChildren() {
		return children.size();
	}
	
	/**
	 * @return The children of this layout composition in an
	 *         unmodifiable collection. Any attempt to modify
	 *         the returned list will throw an exception.
	 */
	public List<Composition> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	/**
	 * Calculates and returns the preferred size of this layout. 
	 * This method should only be called by the UI that owns this 
	 * composition when the current preferred size is invalid. If 
	 * the intention of calling this method is to get the current 
	 * preferred size of the layout, then the cached version of the 
	 * preferred size should instead be gathered by calling the 
	 * function {@link #getPreferredSize(IRenderingContext2D)}.
	 * 
	 * @param context - The current rendering context
	 * 
	 * @return The calculated preferred size of the layout
	 * 
	 * @see #getPreferredSize(IRenderingContext2D)
	 */
	public abstract Vec2i calculateLayoutPreferredSize(IRenderingContext2D context);
	
}
