package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Collider extends Entity {

	protected float age;
	
	public Collider(Vector pos, int r) {
		super(pos, r);
		age = 0;
	}

	public abstract void onCollision(CollisionEvent e);
	
	@Override
	public void update(World world, float deltaTime) {
		age += deltaTime;
		if (age > getLifeSpan()) {
			world.deleteSoft(this);
		}
	}
	
	protected float getLifeSpan() {
		return 3;
	}
		
}
