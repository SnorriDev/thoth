package snorri.entities;

import snorri.collisions.Collider;
import snorri.events.CollisionEvent;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Detector extends Despawner {

	private static final long serialVersionUID = 1L;
	protected boolean treeMember = false;
	
	public Detector(Vector pos, int r) {
		super(pos, r);
	}
	
	public Detector(Entity e) {
		super(e);
	}

	public Detector(Vector pos, Collider collider) {
		super(pos, collider);
	}

	public abstract void onCollision(CollisionEvent e);
	
	@Override
	public void update(World world, double deltaTime) {
		
		for (Entity hit : world.getEntityTree().getAllCollisions(this)) {
			if (hit != null) {
				onCollision(new CollisionEvent(this, hit, world));
			}
		}
		
		super.update(world, deltaTime);
		
	}
	
	/**
	 * @return whether this entity should be stored in the entity tree or in the projectiles list
	 */
	public boolean isTreeMember() {
		return treeMember;
	}
		
}
