package com.g4mesoft.graphic.ui;

import com.g4mesoft.math.Vec2i;

public abstract class UIComponent {

	private Object layoutHint;
	
	protected int x;
	protected int y;
	protected int width;
	protected int height;

	UIComponent parent;
	
	public UIComponent() {
		x = y = width = height = 0;
		
		parent = null;
	}
	
	protected void parentAdded() { 
	}

	protected void parentRemoved() { 
	}
	
	public void add(UIComponent component) {
		add(component, null);
	}
	
	public void add(UIComponent component, Object layoutHint) {
		if (component.parent != null) return;
		
		component.parent = this;
		component.layoutHint = layoutHint;
		
		component.parentAdded();
	}
	
	public void remove(UIComponent component) {
		if (component.parent != this) return;
		
		component.parent = null;
		component.layoutHint = null;
		
		component.parentRemoved();
	}
	
	public void removeAll() {
		
	}
	
	public void layout() {
	}
	
	public void setLayout() {
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public UIComponent getParent() {
		return parent;
	}
	
	public String getName() {
		return "uicomponent";
	}

	public UIMargin getMargin() {
		// TODO implement borders and get margin
		return new UIMargin();
	}

	public Vec2i getMinimumSize() {
		// TODO Auto-generated method stub
		return new Vec2i(1);
	}
}
