package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.events.CollisionEvent;
import snorri.world.Vector;

public class Spike extends Detector {

	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/objects/urn.png");
	
	public Spike(Vector pos) {
		super(pos, new RectCollider(new Vector(10, 26)));
		animation = new Animation(ANIMATION);
	}

	@Override
	public void onCollision(CollisionEvent e) {
		return;
	}

}
