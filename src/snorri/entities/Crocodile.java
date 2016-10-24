package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.world.Vector;

public class Crocodile extends Unit {

	private static final long serialVersionUID = 1L;
	
	public static final Animation IDLE = new Animation("/textures/animations/crocodile/idle");
	public static final Animation ATTACK = new Animation("/textures/animations/crocodile/attack");
	
	public Crocodile(Vector pos) {
		super(pos, new RectCollider(143, 57));
		animation = new Animation(IDLE);
	}

}
