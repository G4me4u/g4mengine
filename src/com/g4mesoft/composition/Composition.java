package com.g4mesoft.composition;

import java.awt.Color;

import com.g4mesoft.composition.ui.CompositionUI;
import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.graphic.IViewport;
import com.g4mesoft.math.MathUtils;
import com.g4mesoft.math.Vec2i;

public abstract class Composition implements IViewport {

	public static final int FILL_PREFERRED = 0;
	public static final int FILL_REMAINING = 1;
	
	public static final float ALIGN_CENTER = 0.5f;

	public static final float ALIGN_LEFT = 0.0f;
	public static final float ALIGN_RIGHT = 1.0f;
	public static final float ALIGN_TOP = 0.0f;
	public static final float ALIGN_BOTTOM = 1.0f;
	
	public static final int BORDER_LEFT = 0x01;
	public static final int BORDER_RIGHT = 0x02;
	public static final int BORDER_TOP = 0x04;
	public static final int BORDER_BOTTOM = 0x08;

	public static final int BORDER_NONE = 0x00;
	public static final int BORDER_HORIZONTAL = BORDER_LEFT | BORDER_RIGHT;
	public static final int BORDER_VERTICAL = BORDER_TOP | BORDER_BOTTOM;
	public static final int BORDER_ALL = BORDER_HORIZONTAL | BORDER_VERTICAL;
	
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
	
	protected int horizontalFill;
	protected int verticalFill;

	protected Color background;
	
	protected int borderFlags;
	protected int borderWidth;
	
	protected Color borderColor;
	
	public Composition() {
		pos = new Vec2i();
		size = new Vec2i();
	
		preferredSize = new Vec2i();
		
		ui = null;
		parent = null;
		
		horizontalAlignment = ALIGN_LEFT;
		verticalAlignment = ALIGN_TOP;
	
		horizontalFill = FILL_PREFERRED;
		verticalFill = FILL_PREFERRED;
		
		background = null;

		borderFlags = BORDER_NONE;
		borderWidth = 1;
		
		borderColor = null;
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
	 * to fill the entire viewport of the context. 
	 * 
	 * @param context - The active rendering context of the application.
	 */
	public final void layout(IRenderingContext2D context) {
		if (parent == null) {
			// We're a root composition.
			pos.set(0, 0);
			size.set(context.getWidth(), context.getHeight());
		}
		
		if (ui != null)
			ui.layoutChanged(context);
		doLayout(context);
		
		valid = true;
		layoutRequested = false;
	}
	
	/**
	 * Layouts this composition. This functions should be overriden by
	 * most sub-implementations, and should be in charge of laying out
	 * the different components of this composition.  
	 * 
	 * @param context - The active rendering context of the application.
	 */
	protected void doLayout(IRenderingContext2D context) {
	}

	public void update() {
		if (ui != null)
			ui.update();
	}
	
	public void render(IRenderer2D renderer, float dt) {
		if (ui != null)
			ui.render(renderer, dt);
	}
	
	@Override
	public int getX() {
		return pos.x;
	}
	
	@Override
	public int getY() {
		return pos.y;
	}
	
	@Override
	public int getWidth() {
		return size.x;
	}
	
	@Override
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
		preferredSize.set(0);

		if (ui != null) {
			Vec2i ps = ui.getPreferredSize(context);
			if (ps != null)
				preferredSize.set(ps);
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
		
		requestRelayout();
	}
	
	public float getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setVerticalAlignment(float alignment) {
		// Clamp alignment between 0 and 1
		verticalAlignment = MathUtils.clamp(alignment, 0.0f, 1.0f);

		requestRelayout();
	}
	
	public float getVerticalAlignment() {
		return verticalAlignment;
	}

	public void setAlignment(float alignment) {
		setVerticalAlignment(alignment);
		setHorizontalAlignment(alignment);
	}
	
	public void setHorizontalFill(int fillmode) {
		if (fillmode != FILL_PREFERRED && fillmode != FILL_REMAINING)
			throw new IllegalArgumentException("Invalid fillmode!");
		
		horizontalFill = fillmode;

		requestRelayout();
	}
	
	public int getHorizontalFill() {
		return horizontalFill;
	}
	
	public void setVerticalFill(int fillmode) {
		if (fillmode != FILL_PREFERRED && fillmode != FILL_REMAINING)
			throw new IllegalArgumentException("Invalid fillmode!");

		verticalFill = fillmode;
		
		requestRelayout();
	}
	
	public int getVerticalFill() {
		return verticalFill;
	}
	
	public void setFill(int fillmode) {
		setHorizontalFill(fillmode);
		setVerticalFill(fillmode);
	}
	
	public void setBackground(Color background) {
		if (background == null && this.background == null)
			return;
		if (background != null && background.equals(this.background))
			return;
		
		this.background = background;
	}
	
	public Color getBackground() {
		return background;
	}

	public void setBorder(int borderFlags) {
		if ((borderFlags & (~BORDER_ALL)) != 0)
			throw new IllegalArgumentException("Invalid border flags.");
	
		this.borderFlags = borderFlags;
	
		requestRelayout();
	}

	public int getBorderFlags() {
		return borderFlags;
	}
	
	public void setBorderWidth(int borderWidth) {
		if (borderWidth <= 0)
			throw new IllegalArgumentException("Border width must be > 0.");
		
		this.borderWidth = borderWidth;

		requestRelayout();
	}
	
	public int getBorderWidth() {
		return borderWidth;
	}
	
	public void setBorderColor(Color borderColor) {
		if (borderColor == null && this.borderColor == null)
			return;
		if (borderColor != null && borderColor.equals(this.borderColor))
			return;
		
		this.borderColor = borderColor;
	}
	
	public Color getBorderColor() {
		return borderColor;
	}
	
	public Composition getParent() {
		return parent;
	}

	public void invalidate() {
		valid = false;
		invalidatePreferredSize();
		
		requestRelayout();
	}
	
	protected void requestRelayout() {
		// We've already requested
		// a re-layout.
		if (layoutRequested)
			return;
		
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
