package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.world.Vector;

public class Desk extends Entity {

	/**
	 * Desks are the stations where you can edit inventory and spells
	 * Press space to interact with them
	 */
	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/objects/inkwell.png");
	public static final int INTERACT_RANGE = 5;
	
	public Desk(Vector pos) {
		super(pos, new RectCollider(12, 27));
		staticObject = true;
		animation = new Animation(ANIMATION);
	}

}
