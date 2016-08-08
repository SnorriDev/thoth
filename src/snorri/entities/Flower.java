package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.world.Vector;

public class Flower extends Plant {

	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/objects/flower.png");

	public Flower(Vector pos) {
		super(pos, new RectCollider(11, 16));
		ignoreCollisions = true;
		animation = new Animation(ANIMATION);
	}

}
