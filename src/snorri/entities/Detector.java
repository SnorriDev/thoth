package snorri.entities;

import snorri.collisions.Collider;
import snorri.events.CollisionEvent;
import snorri.util.Util;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Detector extends Despawner {

	private static final long serialVersionUID = 1L;
	private static final double DETECT_CHANCE = 0.5;
	
	/**
	 * set <code>true</code> iff collisions should be registered against
	 * entities <code>e</code> s.t. <code>e.hitAll = false</code>
	 */
	protected boolean hitAll = false;
	
	public Detector(Vector pos, int r) {
		super(pos, r);
		z = PARTICLE_LAYER;
	}
	
	public Detector(Entity e) {
		super(e);
		z = PARTICLE_LAYER;
	}

	public Detector(Vector pos, Collider collider) {
		super(pos, collider);
		z = PARTICLE_LAYER;
	}

	public abstract void onCollision(CollisionEvent e);
	
	@Override
	public void update(World world, double deltaTime) {
		
		if (Util.flip(DETECT_CHANCE)) {
			world.getEntityTree().mapOverCollisions(this, hitAll, hit -> {
				if (hit != null) {
					onCollision(new CollisionEvent(this, hit, world, deltaTime));
				}
			});
		}
		
		super.update(world, deltaTime);	
	}
	
	/**
	 * @return whether this entity should be stored in the entity tree or in the projectiles list
	 */
	public boolean isTreeMember() {
		return true;
	}
		
}
