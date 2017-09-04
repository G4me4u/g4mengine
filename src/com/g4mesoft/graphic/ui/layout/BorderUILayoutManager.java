package com.g4mesoft.graphic.ui.layout;

import com.g4mesoft.graphic.ui.UIComponent;
import com.g4mesoft.graphic.ui.UIMargin;
import com.g4mesoft.math.Vec2i;

public class BorderUILayoutManager implements UILayoutManager {

	private static final String CENTER = "center";
	private static final String NORTH  = "north";
	private static final String EAST   = "east";
	private static final String SOUTH  = "south";
	private static final String WEST   = "west";
	
	private UIComponent parent;
	
	private UIComponent center;
	private UIComponent north;
	private UIComponent east;
	private UIComponent south;
	private UIComponent west;
	
	private int hgab;
	private int vgab;
	
	public BorderUILayoutManager() {
		this(0, 0);
	}

	public BorderUILayoutManager(int hgab, int vgab) {
		this.hgab = hgab;
		this.vgab = vgab;
	}
	
	public int getHorizontalGab() {
		return hgab;
	}
	
	public void setHorizontalGab(int hgab) {
		this.hgab = hgab;
	}

	public int getVerticalGab() {
		return vgab;
	}

	public void setVerticalGab(int vgab) {
		this.vgab = vgab;
	}
	
	@Override
	public void addLayoutComponent(UIComponent component, Object layoutHint) {
		if (layoutHint == null || !(layoutHint instanceof String))
			throw new IllegalArgumentException("layoutHint is not a string");
		
		switch ((String)layoutHint) {
		case CENTER:
			center = component;
			break;
		case NORTH:
			north = component;
			break;
		case EAST:
			east = component;
			break;
		case SOUTH:
			south = component;
			break;
		case WEST:
			west = component;
			break;
		default:
			throw new IllegalArgumentException("Unknown layoutHint " + layoutHint);
		}
	}

	@Override
	public void removeLayoutComponent(UIComponent component) {
		if (center == component) {
			center = null;
		} else if (north == component) {
			north = null;
		} else if (east == component) {
			east = null;
		} else if (south == component) {
			south = null;
		} else if (west == component) {
			west = null;
		}
	}

	@Override
	public void setParent(UIComponent parent) {
		if (this.parent != null && parent != null) 
			throw new IllegalStateException("Can not assign layout to multiple components");
		this.parent = parent;
	}

	@Override
	public void invalidateLayout() { }

	@Override
	public void layout() {
		
	}

	@Override
	public Vec2i getPreferredSize() {
		Vec2i p = new Vec2i();
		// TODO: implement preferred size
		return p;
	}

	@Override
	public Vec2i getMinimumSize() {
		Vec2i m = new Vec2i();
		if (parent != null) {
			UIMargin margin = parent.getMargin();
			m.add(margin.left + margin.right,
			      margin.top + margin.bottom);
		}
		if (west != null) {
			Vec2i w = west.getMinimumSize();
			m.x += w.x;
			m.y = Math.max(m.y, w.y);
		}
		if (east != null) {
			Vec2i e = east.getMinimumSize();
			m.x += e.x;
			m.y = Math.max(m.y, e.y);
		}
		if (center != null) {
			Vec2i c = center.getMinimumSize();
			m.x += c.x;
			m.y = Math.max(m.y, c.y);
		}
		if (north != null) {
			Vec2i n = north.getMinimumSize();
			m.x = Math.max(m.x, n.x);
			m.y += n.y;
		}
		if (south != null) {
			Vec2i s = south.getMinimumSize();
			m.x = Math.max(m.x, s.x);
			m.y += s.y;
		}
		includeGap(m);
		return m;
	}
	
	private Vec2i includeGap(Vec2i size) {
		//TODO: implement gap thingy
		return null;
	}
}
