package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.world.Vector;
import snorri.world.World;

public class Sarcophagus extends Entity {

	private static final long serialVersionUID = 1L;
	private static final Animation ANIMATION = new Animation("/textures/objects/sarcophagus.png");
	
	public Sarcophagus(Vector pos) {
		super(pos, new RectCollider(new Vector(30, 140)));
		staticObject = true;
		animation = new Animation(ANIMATION);
	}
	
	@Override
	protected void onSafeDelete(World world) {
		world.add(new Mummy(pos.copy()));
	}

}
