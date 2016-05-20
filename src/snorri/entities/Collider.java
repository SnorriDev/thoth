package snorri.entities;

import snorri.world.Vector;

public abstract class Collider extends Entity {

	public Collider(Vector pos, int r) {
		super(pos, r);
	}

	public abstract void onCollision(CollisionEvent e);
		
}
