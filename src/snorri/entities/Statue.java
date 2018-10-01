package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.world.Vector;

public class Statue extends Entity {

	private static final long serialVersionUID = 1L;

	private static final Animation THOTH_STATUE_ANIMATION = new Animation("/textures/objects/statues/thoth.png");
	private static final Animation DEFAULT_STATUE_ANIMATION = THOTH_STATUE_ANIMATION;
	
	public Statue(Vector pos) {
		super(pos, new RectCollider(32, 20));
		setAnimation(DEFAULT_STATUE_ANIMATION);
	}
	
}
