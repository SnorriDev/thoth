package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.world.Vector;

public class Urn extends Despawner {

	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/objects/urn.png");
	
	public Urn(Vector pos, float age) {
		super(pos, new RectCollider(new Vector(10, 26)));
		animation = new Animation(ANIMATION);
		this.age = age;
	}
	
	public Urn(Vector pos) {
		this(pos, -1);
	}

}
