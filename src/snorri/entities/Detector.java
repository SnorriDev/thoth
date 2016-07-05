package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.world.Vector;
import snorri.world.World;

public class Detector extends Entity {

	private static final long serialVersionUID = 1L;
	protected float age; //set age to -1 to make it not despawn
	protected boolean treeMember = false;
	
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
		if (age == -1) {
			return;
		}
		age += deltaTime;
		if (age > getLifeSpan()) {
			world.delete(this);
		}
	}
	
	protected float getLifeSpan() {
		return 4;
	}
	
	/**
	 * @return whether this entity should be stored in the entity tree or in the projectiles list
	 */
	public boolean isTreeMember() {
		return treeMember;
	}
		
}
