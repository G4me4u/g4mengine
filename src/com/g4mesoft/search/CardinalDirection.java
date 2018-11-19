package com.g4mesoft.search;

import com.g4mesoft.math.Vec2f;
import com.g4mesoft.math.Vec2i;

public enum CardinalDirection {

	NORTH(0, 2, 6, 1, 7,      new Vec2i( 0, -1), false), 
	NORTH_EAST(1, 3, 7, 2, 0, new Vec2i( 1, -1), true ), 
	EAST(2, 4, 0, 3, 1,       new Vec2i( 1,  0), false), 
	SOUTH_EAST(3, 5, 1, 4, 2, new Vec2i( 1,  1), true ), 
	SOUTH(4, 6, 2, 5, 3,      new Vec2i( 0,  1), false), 
	SOUTH_WEST(5, 7, 3, 6, 4, new Vec2i(-1,  1), true ), 
	WEST(6, 0, 4, 7, 5,       new Vec2i(-1,  0), false), 
	NORTH_WEST(7, 1, 5, 0, 6, new Vec2i(-1, -1), true );

	private static final CardinalDirection[] DIRECTIONS;

	private final int index;
	private final int cw90, ccw90;
	private final int cw45, ccw45;

	private final Vec2i offset;
	private final boolean diagonal;
	
	private CardinalDirection(int index, int cw90, int ccw90, int cw45, int ccw45, Vec2i offset, boolean diagonal) {
		this.index = index;
		this.cw90 = cw90;
		this.ccw90 = ccw90;
		this.cw45 = cw45;
		this.ccw45 = ccw45;
	
		this.offset = offset;
		this.diagonal = diagonal;
	}
	
	public int getIndex() {
		return index;
	}

	public Vec2i getOffset() {
		return offset;
	}
	
	public boolean isDiagonal() {
		return diagonal;
	}

	public CardinalDirection rotCW90() {
		return DIRECTIONS[cw90];
	}

	public CardinalDirection rotCCW90() {
		return DIRECTIONS[ccw90];
	}
	
	public CardinalDirection rotCW45() {
		return DIRECTIONS[cw45];
	}

	public CardinalDirection rotCCW45() {
		return DIRECTIONS[ccw45];
	}
	
	public CardinalDirection getOpposite() {
		return rotCW90().rotCW90();
	}
	
	public Vec2i offset(Vec2i vector, int amount) {
		return vector.add(offset.x * amount, offset.y * amount);
	}
	
	public Vec2f offset(Vec2f vector, float amount) {
		return vector.add((float)offset.x * amount, (float)offset.y * amount);
	}
	
	public static CardinalDirection fromIndex(int index) {
		return DIRECTIONS[index];
	}

	public static CardinalDirection fromVector(Vec2f dir) {
		if (dir.x > 0.0f) {
			if (dir.y > 0.0f)
				return SOUTH_EAST;
			if (dir.y == 0.0f)
				return EAST;
			return NORTH_EAST;
		}

		if (dir.x < 0.0f) {
			if (dir.y > 0.0f)
				return SOUTH_WEST;
			if (dir.y == 0.0f)
				return WEST;
			return NORTH_WEST;
		}
		
		return dir.y > 0.0f ? SOUTH : NORTH;
	}
	
	public static CardinalDirection fromVector(Vec2i dir) {
		if (dir.x > 0) {
			if (dir.y > 0)
				return SOUTH_EAST;
			if (dir.y == 0)
				return EAST;
			return NORTH_EAST;
		}
		
		if (dir.x < 0) {
			if (dir.y > 0)
				return SOUTH_WEST;
			if (dir.y == 0)
				return WEST;
			return NORTH_WEST;
		}
		
		return dir.y > 0 ? SOUTH : NORTH;
	}
	
	static {
		DIRECTIONS = new CardinalDirection[values().length];
		for (CardinalDirection dir : values())
			DIRECTIONS[dir.index] = dir;
	}
}
