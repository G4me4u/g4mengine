package com.g4mesoft.world.entity;

import com.g4mesoft.math.Vec2f;
import com.g4mesoft.world.World;

public abstract class Entity {

	public Vec2f pos;
	public Vec2f prevPos;
	
	protected boolean dead;

	public final World world;
	
	protected Entity(World world) { 
		this.world = world;
		
		pos = new Vec2f();
		prevPos = new Vec2f();
	}

	protected abstract void update();
	
	public final void tick() {
		prevPos.set(pos);
		
		update();
	}

	public void setDead() {
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
}
