package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.world.Vector;

public class Explosion extends Collider {

	private int damage = 100;
	
	public Explosion(Vector pos) {
		super(pos, 16);
	}
	
	public Explosion(Vector pos, int r) {
		super(pos, r);
	}
	
	public Explosion(Vector pos, int r, int damage) {
		super(pos, r);
		this.damage = damage;
	}

	@Override
	public void onCollision(CollisionEvent e) {
		if (e.getTarget() instanceof Unit) {
			((Unit) e.getTarget()).damage(damage);
		}
	}
	
	@Override
	protected float getLifeSpan() {
		return 0.3f;
	}

}
