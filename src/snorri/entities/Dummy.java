package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.world.Vector;

/**
 * A class for displaying and testing animations in the editor.
 * @author snorri
 * Can also be used for displaying very specific textured entities that don't merit a full class.
 * This secondary use, however, is deprecated.
 */

public class Dummy extends Entity {


	private static final long serialVersionUID = 1L;

	public Dummy(Vector pos, String animation) {
		this(pos, new Animation(animation));
	}
	
	public Dummy(Vector pos, Animation animation) {
		super(pos, new RectCollider(animation.getWidth(), animation.getHeight()));
		setAnimation(animation);
	}
	
}
