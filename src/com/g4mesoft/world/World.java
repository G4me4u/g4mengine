package com.g4mesoft.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.g4mesoft.world.entity.Collidable;
import com.g4mesoft.world.entity.Entity;
import com.g4mesoft.world.entity.LivingEntity;
import com.g4mesoft.world.phys.AABB;

public abstract class World {

	public long worldTime;
	
	protected List<Entity> entities;
	private List<Entity> entitiesToAdd;
	protected boolean updatingEntities;
	
	public World() {
		worldTime = 0L;
		
		entities = new ArrayList<Entity>();
		entitiesToAdd = new ArrayList<Entity>();
		updatingEntities = false;
	}
	
	public synchronized void update() {
		updateEntityList(entities);
		incrementWorldTime(1L);
	}
	
	protected void updateEntityList(List<Entity> entities) {
		updatingEntities = true;
		synchronized (entities) {
			Iterator<Entity> entityIterator = entities.iterator();
			while (entityIterator.hasNext()) {
				Entity entity = entityIterator.next();
				entity.tick();
				if (entity.isDead())
					entityIterator.remove();
			}
		}
		updatingEntities = false;

		synchronized (entitiesToAdd) {
			if (entitiesToAdd.size() > 0) {
				entities.addAll(entitiesToAdd);
				entitiesToAdd.clear();
			}
		}
	}
	
	public synchronized void addEntity(Entity e) {
		if (updatingEntities) {
			synchronized (entitiesToAdd) {
				entitiesToAdd.add(e);
			}
		} else {
			synchronized (entities) {
				entities.add(e);
			}
		}
	}
	
	public synchronized Entity removeEntity(Entity e) {
		e.setDead();

		if (updatingEntities) {
			return e;
		} else {
			synchronized (entities) {
				int index = entities.indexOf(e);
				if (index >= 0) {
					return entities.remove(index);
				}
			}
		}
		return null;
	}
	
	protected synchronized void killAllEntities() {
		synchronized (entities) {
			for (Entity e : entities)
				e.setDead();

			if (!updatingEntities)
				entities.clear();
		}
		
		if (!updatingEntities) {
			/* Only this class has power
			 * over entitiesToAdd, so it
			 * wont be changing during this
			 * operation.
			 */
			synchronized (entitiesToAdd) {
				entitiesToAdd.clear();
			}
		}
	}

	public void incrementWorldTime(long amount) {
		worldTime += amount;
	}
	
	public List<Entity> getEntityList() {
		return entities;
	}
	
	public boolean isUpdatingEntities() {
		return updatingEntities;
	}
	
	public abstract boolean isClient();

	public List<Entity> getCollidingEntities(LivingEntity entity) {
		return getCollidingEntities(entity.getBody());
	}
	
	public synchronized List<Entity> getCollidingEntities(AABB body) {
		List<Entity> collidingEntities = new ArrayList<Entity>();
		
		for (Entity e : entities) {
			if (!(e instanceof Collidable)) continue;
			
			AABB bb1 = ((Collidable) e).getBody();
			if (body.collides(bb1))
				collidingEntities.add((Entity)e);
		}
		
		return collidingEntities;
	}
}
