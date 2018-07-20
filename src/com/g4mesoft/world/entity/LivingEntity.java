package com.g4mesoft.world.entity;

import com.g4mesoft.world.World;
import com.g4mesoft.world.phys.AABB;

public abstract class LivingEntity extends Entity implements ICollidable {

	protected AABB body;
	
	public LivingEntity(World world) {
		super(world);
		
		body = createBody();
	}
	
	protected abstract AABB createBody();

	public AABB getBody() {
		return body;
	}
}
