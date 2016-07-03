package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.world.Vector;
import snorri.world.World;

public class Detector extends Entity {

	private static final long serialVersionUID = 1L;
	protected float age;
	
	public Detector(Vector pos, int r) {
		super(pos, r);
		age = 0;
	}
	
	public Detector(Entity e) {
		super(e);
		age = 0;
	}

	public void onCollision(CollisionEvent e) {
	}
	
	@Override
	public void update(World world, double deltaTime) {
		age += deltaTime;
		if (age > getLifeSpan()) {
			world.delete(this);
		}
	}
	
	protected float getLifeSpan() {
		return 4;
	}
		
}
