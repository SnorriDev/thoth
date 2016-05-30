package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Collider extends Entity {

	private int age = 0;
	
	public Collider(Vector pos, int r) {
		super(pos, r);
	}

	public abstract void onCollision(CollisionEvent e);
	
	public void update(World world, int deltaTime) {
		
		age += deltaTime;
		if (age > getLifeSpan()) {
			world.deleteSoft(this);
		}
		
	}
	
	protected int getLifeSpan() {
		return 4000;
	}
		
}
