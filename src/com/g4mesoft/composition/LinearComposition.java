package com.g4mesoft.composition;

import java.awt.Color;

import com.g4mesoft.graphic.IRenderer2D;
import com.g4mesoft.graphic.IRenderingContext2D;
import com.g4mesoft.math.Vec2i;

public class LinearComposition extends LayoutComposition {

	public static final int HORIZONTAL_DIRECTION = 0;
	public static final int VERTICAL_DIRECTION = 1;
	
	private int direction;
	private int gap;
	
	public LinearComposition() {
		this(HORIZONTAL_DIRECTION);
	}
	
	public LinearComposition(int direction) {
		this(direction, 0);
	}

	public LinearComposition(int direction, int gap) {
		this.direction = getLayoutDirection(direction);
		this.gap = gap;
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
		boolean layoutAll = !valid;
		
		super.layout(context);

		int n = children.size();
		if (n == 0)
			return;
		
		if (direction == HORIZONTAL_DIRECTION) {
			int x = pos.x;
			int remainingWidth = size.x - gap * (n - 1);
			for (Composition child : children) {
				int width = remainingWidth / n--;
				if (layoutAll || child.isRelayoutRequired()) {
					if (child.horizontalFill == FILL_PREFERRED || child.verticalFill == FILL_PREFERRED) {
						Vec2i ps = child.getPreferredSize(context);
						
						child.size.x = child.horizontalFill == FILL_PREFERRED ? ps.x : width;
						child.size.y = child.verticalFill == FILL_PREFERRED ? ps.y : size.y;
					} else {
						child.size.set(width, size.y);
					}
					
					child.pos.x = x + (int)((width - child.size.x + 0.5f) * child.horizontalAlignment);
					child.pos.y = pos.y + (int)((size.y - child.size.y + 0.5f) * child.verticalAlignment);
					
					child.layout(context);
				}

				x += width + gap;
				remainingWidth -= width;
			}
		} else {
			int y = pos.y;
			int remainingHeight = size.y - gap * (n - 1);
			for (Composition child : children) {
				int height = remainingHeight / n--;

				if (layoutAll || child.isRelayoutRequired()) {
					if (child.horizontalFill == FILL_PREFERRED || child.verticalFill == FILL_PREFERRED) {
						Vec2i ps = child.getPreferredSize(context);
						
						child.size.x = child.horizontalFill == FILL_PREFERRED ? ps.x : size.x;
						child.size.y = child.verticalFill == FILL_PREFERRED ? ps.y : height;
					} else {
						child.size.set(size.x, height);
					}
					
					child.pos.x = pos.x + (int)((size.x - child.size.x + 0.5f) * child.horizontalAlignment);
					child.pos.y = y + (int)((height - child.size.y + 0.5f) * child.verticalAlignment);
					
					child.layout(context);
				}

				y += height + gap;
				remainingHeight -= height;
			}
		}
	}
	
	@Override
	public void render(IRenderer2D renderer, float dt) {
		renderer.setColor(Color.BLACK);
		renderer.fillRect(getY(), getX(), getWidth(), getHeight());

		super.render(renderer, dt);
	}
	
	@Override
	protected void calculatePreferredSize(Vec2i preferredSize, IRenderingContext2D context) {
		int numChildren = children.size();
		if (numChildren == 0) {
			preferredSize.set(0);
			return;
		}
		
		if (direction == HORIZONTAL_DIRECTION) {
			preferredSize.set(gap * (numChildren - 1), 0);
		} else {
			preferredSize.set(0, gap * (numChildren - 1));
		}
		
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
	}
}
