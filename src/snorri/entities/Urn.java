package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.semantics.Break.Smashable;
import snorri.world.Vector;

public class Urn extends Despawner implements Smashable {

	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/objects/urn.png");
	
	public Urn(Vector pos) {
		super(pos, new RectCollider(new Vector(10, 26)));
		animation = new Animation(ANIMATION);
	}

}
