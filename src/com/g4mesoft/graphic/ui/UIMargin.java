package com.g4mesoft.graphic.ui;

public class UIMargin {

	public int top;
	public int right;
	public int bottom;
	public int left;
	
	public UIMargin() {
		this(0, 0, 0, 0);
	}
	
	public UIMargin(int top, int right, int bottom, int left) {
		set(top, right, bottom, left);
	}
	
	public void set(int top, int right, int bottom, int left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}
}
