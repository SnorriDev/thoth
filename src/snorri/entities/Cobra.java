package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.world.Vector;

public class Cobra extends Unit {

	private static final long serialVersionUID = 1L;
	
	public static final Animation IDLE = new Animation("/textures/animations/cobra");
	
	public Cobra(Vector pos) {
		super(pos, new RectCollider(57, 51));
		animation = new Animation(IDLE);
	}
	
}
