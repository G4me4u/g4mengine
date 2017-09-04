package com.g4mesoft.graphic.ui.layout;

import com.g4mesoft.graphic.ui.UIComponent;
import com.g4mesoft.math.Vec2i;

public interface UILayoutManager {

	public void addLayoutComponent(UIComponent component, Object layoutHint);
	
	public void removeLayoutComponent(UIComponent component);
	
	public void setParent(UIComponent parent);

	public void invalidateLayout();

	public void layout();
	
	public Vec2i getPreferredSize();

	public Vec2i getMinimumSize();
	
}
