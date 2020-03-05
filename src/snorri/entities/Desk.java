package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.world.Vector;

public class Desk extends Entity {

	/**
	 * Formerly stations for editing inventory, desks are now just scenery.
	 */
	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/objects/inkwell.png");
	
	public Desk(Vector pos) {
		super(pos, new RectCollider(12, 27));
		animation = new Animation(ANIMATION);
	}

}