package snorri.entities;

public class CollisionEvent {

	private Collider source; //the thing that has collided with something
	private Entity target; //the thing that it hit
	
	public CollisionEvent(Collider source, Entity target) {
		this.source = source;
		this.target = target;
	}
	
	public Collider getSource() {
		return source;
	}
	
	public Entity getTarget() {
		return target;
	}
	
}
