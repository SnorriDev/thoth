package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.world.Vector;

public class Urn extends Entity {

	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/objects/urn.png");
	
	public Urn(Vector pos) {
		super(pos, new RectCollider(new Vector(10, 13)));
		animation = new Animation(ANIMATION);
	}

}
