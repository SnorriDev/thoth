package snorri.entities;

import snorri.collisions.Collider;
import snorri.world.Vector;

public abstract class Plant extends Entity {

	private static final long serialVersionUID = 1L;
	
	public Plant(Vector pos, Collider collider) {
		super(pos, collider);
	}

}
