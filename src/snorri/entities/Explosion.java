package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.world.Vector;

public class Explosion extends Collider {

	public Explosion(Vector pos, int r) {
		super(pos, r);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCollision(CollisionEvent e) {
		
	}

}
