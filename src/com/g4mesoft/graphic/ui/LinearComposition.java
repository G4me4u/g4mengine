package com.g4mesoft.graphic.ui;

import java.awt.Color;

import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public class LinearComposition extends LayoutComposition {

	public static final int HORIZONTAL_DIRECTION = 0;
	public static final int VERTICAL_DIRECTION = 1;
	
	private int direction;
	
	public LinearComposition() {
		this(HORIZONTAL_DIRECTION);
	}
	
	public LinearComposition(int direction) {
		this.direction = getLayoutDirection(direction);
	}

	private static int getLayoutDirection(int direction) {
		switch(direction) {
		case HORIZONTAL_DIRECTION:
		case VERTICAL_DIRECTION:
			return direction;
		}
		
		throw new IllegalArgumentException("Invalid direction");
	}
	
	@Override
	public void layout(IRenderingContext2D context) {
		super.layout(context);
		
		for (Composition child : children)
			child.layout(context);
	}
	
	@Override
	public void render(IRenderer2D renderer, float dt) {
		renderer.setColor(Color.BLACK);
		renderer.fillRect(getY(), getX(), getWidth(), getHeight());

		super.render(renderer, dt);
	}
	
	@Override
	public Vec2i getPreferredSize(IRenderingContext2D context) {
		Vec2i preferredSize = new Vec2i();
		for (Composition child : children) {
			Vec2i ps = child.getPreferredSize(context);
			if (direction == HORIZONTAL_DIRECTION) {
				preferredSize.x += ps.x;
				if (ps.y > preferredSize.y)
					preferredSize.y = ps.y;
			} else {
				if (ps.x > preferredSize.x)
					preferredSize.x = ps.x;
				preferredSize.y += ps.y;
			}
		}
		return preferredSize.x == 0 && preferredSize.y == 0 ? null : preferredSize;
	}
}
