package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.CircleCollider;
import snorri.world.Vector;

public class Center extends Entity {
	
	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/objects/center00.png");

	
	public Center(Vector pos) {
		super(pos, new CircleCollider(16));
		ignoreCollisions = true;
		animation = new Animation(ANIMATION);
	}
	
	@Override
	public boolean hasGravity() {
		return false;
	}
	
	@Override
	public boolean isStaticObject() {
		return true;
	}
	
}
