package com.g4mesoft.composition;

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
	public void doLayout(IRenderingContext2D context) {
		int numChildren = children.size();
		if (numChildren == 0)
			return;
		
		// Number of children that have
		// to fill the remaining width and
		// height.
		int nw = numChildren;
		int nh = numChildren;
		
		Vec2i remainingSize = new Vec2i(size);
		remainingSize.sub(gap * (numChildren - 1));
		
		for (Composition child : children) {
			Vec2i ps = child.getPreferredSize(context);

			if (child.horizontalFill == FILL_PREFERRED) {
				remainingSize.x -= ps.x;
				nw--;
			}
			
			if (child.verticalFill == FILL_PREFERRED) {
				remainingSize.y -= ps.y;
				nh--;
			}
			
			BorderInsets insets = child.getBorderInsets();
			remainingSize.x -= insets.getHorizontalInsets();
			remainingSize.y -= insets.getVerticalInsets();
		}

		// The number of children that
		// we have to lay out.
		int n = numChildren;
		
		if (direction == HORIZONTAL_DIRECTION) {
			// The remaining width for the compositions 
			// that have to fill the remaining part of
			// the layout. Or the amount of width that 
			// has to be taken away from the other 
			// children (if negative).
			int remainingWidth = remainingSize.x;
			
			int xa = pos.x;
			for (Composition child : children) {
				Vec2i ps = child.getPreferredSize(context);
				BorderInsets insets = child.getBorderInsets();
				
				int x = xa;
				int width = 0;
				
				if (child.horizontalFill != FILL_PREFERRED) {
					// If the remaining width is negative
					// (or there is no remaining width),
					// the width of the child is zero.
					if (remainingWidth > 0) {
						width = remainingWidth / nw--;
						remainingWidth -= width;
					}
				} else {
					width = ps.x;
					
					if (remainingWidth < 0) {
						// Remaining width is negative.
						// we have to take the width from
						// the preferred sized children.
						int rw = remainingWidth / (numChildren - nw);
						remainingWidth -= rw;
						width += rw;
						
						// The number of children with a
						// non-preferred size has changed.
						nw++;
					} else if (nw == 0) {
						// There are no children to take
						// up the remaining width. We should
						// therefore align the preferred sized
						// children horizontally.
						int rw = remainingWidth / n;
						remainingWidth -= rw;

						x += (int)(rw * child.horizontalAlignment);
						xa += rw;
					}
				}

				int y = pos.y;
				int height = size.y - insets.getVerticalInsets();

				// We should only use the preferred
				// height, if it's actually available
				// in the height of the layout.
				if (child.verticalFill == FILL_PREFERRED && ps.y < height) {
					height = ps.y;

					// Only in the vertical axis
					// do we need to align the
					// composition.
					y += (int)((size.y - ps.y) * child.verticalAlignment);
				}

				x += insets.left;
				y += insets.top;
				
				xa += insets.getHorizontalInsets();
				
				// Only invalidate and layout the
				// child, if the size or position
				// changed. This is important for
				// optimization.
				if (!child.pos.equals(x, y) || !child.size.equals(width, height)) {
					child.pos.set(x, y);
					child.size.set(width, height);
					
					child.invalidate();
				}

				child.layout(context);
				
				xa += width + gap;
				n--;
			}
		} else {
			int remainingHeight = remainingSize.y;

			int ya = pos.y;
			for (Composition child : children) {
				Vec2i ps = child.getPreferredSize(context);
				BorderInsets insets = child.getBorderInsets();

				int x = pos.x;
				int width = size.x - insets.getHorizontalInsets();

				if (child.horizontalFill == FILL_PREFERRED && ps.x < width) {
					width = ps.x;
					x += (int)((size.x - ps.x) * child.horizontalAlignment);
				}
				
				int y = ya;
				int height = 0;
				
				if (child.verticalFill != FILL_PREFERRED) {
					if (remainingHeight > 0) {
						height = remainingHeight / nh--;
						remainingHeight -= height;
					}
				} else {
					height = ps.y;
					
					if (remainingHeight < 0) {
						int rh = remainingHeight / (numChildren - nh);
						remainingHeight -= rh;
						height += rh;
						nh++;
					} else if (nh == 0) {
						int rh = remainingHeight / n;
						remainingHeight -= rh;

						y += (int)(rh * child.verticalAlignment);
						ya += rh;
					}
				}
				
				x += insets.left;
				y += insets.top;

				ya += insets.getVerticalInsets();
				
				if (!child.pos.equals(x, y) || !child.size.equals(width, height)) {
					child.pos.set(x, y);
					child.size.set(width, height);
					
					child.invalidate();
				}
				
				child.layout(context);

				ya += height + gap;
				n--;
			}
		}
	}
	
	@Override
	public Vec2i calculateLayoutPreferredSize(IRenderingContext2D context) {
		Vec2i preferredSize = new Vec2i();
		
		int numChildren = children.size();
		if (numChildren == 0)
			return preferredSize;
		
		if (direction == HORIZONTAL_DIRECTION) {
			preferredSize.set(gap * (numChildren - 1), 0);
		} else {
			preferredSize.set(0, gap * (numChildren - 1));
		}
		
		for (Composition child : children) {
			Vec2i ps = child.getPreferredSize(context);
			BorderInsets insets = child.getBorderInsets();
			
			int px = ps.x + insets.getHorizontalInsets();
			int py = ps.y + insets.getVerticalInsets();
			
			if (direction == HORIZONTAL_DIRECTION) {
				preferredSize.x += px;
				if (py > preferredSize.y && child.getVerticalFill() != FILL_REMAINING)
					preferredSize.y = py;
			} else {
				if (px > preferredSize.x && child.getHorizontalFill() != FILL_REMAINING)
					preferredSize.x = px;
				preferredSize.y += py;
			}
		}
		
		return preferredSize;
	}
}
