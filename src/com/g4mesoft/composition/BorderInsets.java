package com.g4mesoft.composition;

public class BorderInsets {

	public int left;
	public int right;
	public int top;
	public int bottom;

	public BorderInsets() {
		this(0, 0, 0, 0);
	}

	public BorderInsets(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}
	
	public void set(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	public int getLeft() {
		return left;
	}
	
	public int getRight() {
		return right;
	}
	
	public int getTop() {
		return top;
	}
	
	public int getBottom() {
		return bottom;
	}
	
	public int getHorizontalInsets() {
		return left + right;
	}

	public int getVerticalInsets() {
		return top + bottom;
	}
}
