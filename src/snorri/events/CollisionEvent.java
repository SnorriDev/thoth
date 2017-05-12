package snorri.events;

import snorri.entities.Detector;
import snorri.entities.Entity;
import snorri.world.World;

public class CollisionEvent {

	private Detector source; //the thing that has collided with something
	private Entity target; //the thing that it hit
	private World world;
	private double deltaTime;
	
	public CollisionEvent(Detector source, Entity target, World world, double deltaTime) {
		this.source = source;
		this.target = target;
		this.world = world;
		this.deltaTime = deltaTime;
	}
	
	public Detector getSource() {
		return source;
	}
	
	public Entity getTarget() {
		return target;
	}
	
	public World getWorld() {
		return world;
	}
	
	public double getDeltaTime() {
		return deltaTime;
	}
	
}
