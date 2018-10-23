package com.g4mesoft.graphic.ui;

import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public abstract class Composition {

	public static final int ALIGN_CENTER = 0;

	public static final int ALIGN_LEFT = 1;
	public static final int ALIGN_RIGHT = 2;
	public static final int ALIGN_TOP = 3;
	public static final int ALIGN_BOTTOM = 4;
	
	protected final Vec2i pos;
	protected final Vec2i size;

	protected boolean preferredSizeInvalid;
	private final Vec2i preferredSize;
	
	private Composition parent;
	
	protected boolean valid;

	protected int horizontalAlignment;
	protected int verticalAlignment;
	
	public Composition() {
		pos = new Vec2i();
		size = new Vec2i();
	
		preferredSize = new Vec2i();
		
		parent = null;
		
		horizontalAlignment = ALIGN_LEFT;
		verticalAlignment = ALIGN_TOP;
	}

	/**
	 * Layouts this composition. If the parent is null, this
	 * function will set the size and position of the composition
	 * to fill the entire viewport of the context. Almost all 
	 * sub-implementations should override this function.
	 * <br><br>
	 * <b>NOTE:</b><i> If this function is overridden, the sub-class
	 * should either call {@code super.layout(IRenderingContext2D)}
	 * or make sure to set valid to true, as follows:</i>
	 * <pre>
	 *     valid = true;
	 * </pre>
	 * 
	 * @param context - The active rendering context of the application.
	 */
	public void layout(IRenderingContext2D context) {
		if (parent == null) {
			// We're a root composition.
			pos.set(0, 0);
			size.set(context.getWidth(), context.getHeight());
		}
		
		valid = true;
	}

	public void render(IRenderer2D renderer, float dt) {
	}
	
	public int getX() {
		return pos.x;
	}
	
	public int getY() {
		return pos.y;
	}
	
	public int getWidth() {
		return size.x;
	}
	
	public int getHeight() {
		return size.y;
	}

	public Vec2i getPreferredSize(IRenderingContext2D context) {
		if (preferredSizeInvalid) {
			calculatePreferredSize(preferredSize, context);
			preferredSizeInvalid = false;
		}
		return preferredSize;
	}
	
	protected void invalidatePreferredSize() {
		preferredSizeInvalid = true;
	}
	
	protected void calculatePreferredSize(Vec2i preferredSize, IRenderingContext2D context) {
		preferredSize.set(0);
	}
	
	public void setParent(Composition composition) {
		parent = composition;
		invalidate();
	}

	public void setHorizontalAlignment(int alignment) {
		switch (alignment) {
		case ALIGN_LEFT:
		case ALIGN_RIGHT:
		case ALIGN_CENTER:
			horizontalAlignment = alignment;
			break;
		default:
			throw new IllegalArgumentException("Invalid alignment");
		}
		
		// The parent should update
		// the layout.
		if (parent != null)
			parent.invalidate();
	}

	public void setVerticalAlignment(int alignment) {
		switch (alignment) {
		case ALIGN_TOP:
		case ALIGN_BOTTOM:
		case ALIGN_CENTER:
			verticalAlignment = alignment;
			break;
		default:
			throw new IllegalArgumentException("Invalid alignment");
		}

		// The parent should update
		// the layout.
		if (parent != null)
			parent.invalidate();
	}
	
	public Composition getParent() {
		return parent;
	}

	public void invalidate() {
		valid = false;
		invalidatePreferredSize();
	}
	
	public boolean isValid() {
		return valid;
	}

	public Composition getCompositionAt(int x, int y) {
		if (x < pos.x || y < pos.y || x >= pos.x + size.x || y >= pos.y + size.y)
			return null;
		return this;
	}
}
