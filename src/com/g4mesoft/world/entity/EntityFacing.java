package com.g4mesoft.world.entity;

import com.g4mesoft.math.Vec2f;
import com.g4mesoft.math.Vec2i;

public enum EntityFacing {

	UP(0, "up",       2, 1, 3, new Vec2f( 0.0f, -1.0f)), 
	RIGHT(1, "right", 3, 2, 0, new Vec2f( 1.0f,  0.0f)), 
	DOWN(2, "down",   0, 3, 1, new Vec2f( 0.0f,  1.0f)),
	LEFT(3, "left",   1, 0, 2, new Vec2f(-1.0f,  0.0f)); 
	
	private static final EntityFacing[] ENTITY_FACES;
	
	private final int index;
	private final String name;
	
	private final int invIndex;
	private final int cwIndex;
	private final int ccwIndex;
	
	private final Vec2f offset;
	
	private EntityFacing(int index, String name, int invIndex, int cwIndex, int ccwIndex, Vec2f offset) {
		this.index = index;
		this.name = name;
		
		this.invIndex = invIndex;
		this.cwIndex = cwIndex;
		this.ccwIndex = ccwIndex;
		
		this.offset = offset;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
	
	public Vec2f getOffset() {
		return offset;
	}

	public EntityFacing invert() {
		return ENTITY_FACES[invIndex];
	}
	
	public EntityFacing rotateCW() {
		return ENTITY_FACES[cwIndex];
	}

	public EntityFacing rotateCCW() {
		return ENTITY_FACES[ccwIndex];
	}

	public static EntityFacing fromIndex(int index) {
		return ENTITY_FACES[index];
	}
	
	public static EntityFacing fromVector(Vec2f vector) {
		if (vector.x > 0.0f) return RIGHT;
		if (vector.x < 0.0f) return LEFT;

		if (vector.y > 0.0f) return DOWN;
		
		return UP;
	}
	
	public static EntityFacing fromVector(Vec2i vector) {
		if (vector.x > 0) return RIGHT;
		if (vector.x < 0) return LEFT;

		if (vector.y > 0) return DOWN;
		
		return UP;
	}
	
	public void move(Vec2f pos) {
		pos.add(offset);
	}

	public void move(Vec2f pos, float amount) {
		pos.add(offset.x * amount, offset.y * amount);
	}

	static {
		ENTITY_FACES = new EntityFacing[values().length];
		
		for (EntityFacing facing : values())
			ENTITY_FACES[facing.index] = facing;
	}
}
