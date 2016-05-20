package snorri.entities;

public class CollisionEvent {

	private Collider source; //the thing that has collided with something
	private Entity target; //the thing that it hit
	private Entity root; //the entity that created the collider
	
	public CollisionEvent(Collider source, Entity target, Entity root) {
		this.source = source;
		this.target = target;
		this.root = root;
	}
	
	public Collider getSource() {
		return source;
	}
	
	public Entity getTarget() {
		return target;
	}
	
	public Entity getRoot() {
		return root;
	}
	
}
