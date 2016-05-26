package snorri.events;

import snorri.entities.Collider;
import snorri.entities.Entity;
import snorri.world.World;

public class CollisionEvent {

	private Collider source; //the thing that has collided with something
	private Entity target; //the thing that it hit
	private World world;
	
	public CollisionEvent(Collider source, Entity target, World world) {
		this.source = source;
		this.target = target;
		this.world = world;
	}
	
	public Collider getSource() {
		return source;
	}
	
	public Entity getTarget() {
		return target;
	}
	
	public World getWorld() {
		return world;
	}
	
}
