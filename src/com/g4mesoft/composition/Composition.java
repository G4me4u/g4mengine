package com.g4mesoft.composition;

import com.g4mesoft.composition.ui.CompositionUI;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec2i;

public abstract class Composition {

	public static final float ALIGN_CENTER = 0.5f;

	public static final float ALIGN_LEFT = 0.0f;
	public static final float ALIGN_RIGHT = 1.0f;
	public static final float ALIGN_TOP = 0.0f;
	public static final float ALIGN_BOTTOM = 1.0f;
	
	protected final Vec2i pos;
	protected final Vec2i size;

	private boolean preferredSizeInvalid;
	private boolean preferredSizeSet;
	private final Vec2i preferredSize;
	
	private CompositionUI ui;
	private Composition parent;
	
	protected boolean valid;
	protected boolean layoutRequested;
	
	protected float horizontalAlignment;
	protected float verticalAlignment;
	
	public Composition() {
		pos = new Vec2i();
		size = new Vec2i();
	
		preferredSize = new Vec2i();
		
		ui = null;
		parent = null;
		
		horizontalAlignment = ALIGN_LEFT;
		verticalAlignment = ALIGN_TOP;
	}

	public void setUI(CompositionUI ui) {
		// Unbind current ui
		CompositionUI oldUI = this.ui;
		if (oldUI != null)
			oldUI.unbindUI(this);
		
		this.ui = ui;
		ui.bindUI(this);

		// We would have to re-layout
		invalidate();
	}
	
	/**
	 * Layouts this composition. If the parent is null, this
	 * function will set the size and position of the composition
	 * to fill the entire viewport of the context. Almost all 
	 * sub-implementations should override this function.
	 * <br><br>
	 * <b>NOTE:</b><i> If this function is overridden, the sub-class
	 * should call {@code super.layout(IRenderingContext2D)}</i>
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
		layoutRequested = false;
	}

	public void update() {
		if (ui != null)
			ui.update();
	}
	
	public void render(IRenderer2D renderer, float dt) {
		if (ui != null)
			ui.render(renderer, dt);
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

	public void setPreferredSize(Vec2i preferredSize) {
		preferredSizeSet = true;
		this.preferredSize.set(preferredSize);
	}
	
	public Vec2i getPreferredSize(IRenderingContext2D context) {
		if (preferredSizeInvalid) {
			if (!preferredSizeSet)
				calculatePreferredSize(preferredSize, context);
			preferredSizeInvalid = false;
		}
		return preferredSize;
	}
	
	protected void invalidatePreferredSize() {
		preferredSizeInvalid = true;
	}
	
	protected void calculatePreferredSize(Vec2i preferredSize, IRenderingContext2D context) {
		if (ui != null) {
			preferredSize.set(ui.getPreferredSize(context));
		} else {
			preferredSize.set(0);
		}
	}
	
	public boolean isPreferredSizeSet() {
		return preferredSizeSet;
	}
	
	public void setParent(Composition composition) {
		if (parent == this)
			throw new IllegalArgumentException("Can not set parent to self!");
		
		parent = composition;
		invalidate();
	}

	public void setHorizontalAlignment(float alignment) {
		// Clamp alignment between 0 and 1
		horizontalAlignment = MathUtils.clamp(alignment, 0.0f, 1.0f);
		
		// The parent should update
		// the layout.
		if (parent != null)
			parent.invalidate();
	}
	
	public float getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setVerticalAlignment(float alignment) {
		// Clamp alignment between 0 and 1
		verticalAlignment = MathUtils.clamp(alignment, 0.0f, 1.0f);

		// The parent should update
		// the layout.
		if (parent != null)
			parent.invalidate();
	}
	
	public float getVerticalAlignment() {
		return verticalAlignment;
	}
	
	public Composition getParent() {
		return parent;
	}

	public void invalidate() {
		valid = false;
		invalidatePreferredSize();
		
		if (parent != null)
			parent.requestRelayout();
	}
	
	protected void requestRelayout() {
		layoutRequested = true;
		
		if (parent != null)
			parent.requestRelayout();
	}
	
	public boolean isRelayoutRequired() {
		return layoutRequested || !valid;
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
