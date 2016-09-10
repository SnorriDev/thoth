package snorri.entities;

import snorri.collisions.Collider;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Despawner extends Entity {
	
	protected static final int DEFAULT_LIFESPAN = 4;

	protected float age; //set age to -1 to make it not despawn
	
	protected Despawner(Vector pos, int r) {
		this(pos, r, false);
	}
		
	protected Despawner(Entity e) {
		super(e);
	}
	
	protected Despawner(Vector pos, Collider c) {
		super(pos, c);
	}
	
	protected Despawner(Vector pos, int r, boolean despawn) {
		super(pos, r);
		age = despawn ? 0 : -1;
		staticObject = despawn;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void update(World world, double deltaTime) {
		
		if (age != -1) {
			age += deltaTime;
		}
		
		if (shouldDespawn()) {
			world.delete(this);
			return;
		}
		
	}
	
	protected boolean shouldDespawn() {
		return age > getLifeSpan();
	}
	
	protected double getLifeSpan() {
		return DEFAULT_LIFESPAN;
	}
	
}
