package com.g4mesoft.search;

import com.g4mesoft.math.Vec2f;

public interface IPositionFilter {

	public boolean isValidPos(Vec2f pos, CardinalDirection dir, int step);
		
}
