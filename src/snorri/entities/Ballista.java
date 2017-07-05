package snorri.entities;

import snorri.animations.Animation;
import snorri.world.Vector;

public class Ballista extends Entity {

	private static final long serialVersionUID = 1L;
	
	private static final Animation IDLE = new Animation("/textures/animations/ballista/idle");
	private static final Animation SHOOT = new Animation("/textures/animations/ballista/shoot");
	
	public Ballista(Vector pos, Vector dir) {
		super(pos, 35);
		setAnimation(IDLE);
		setDirection(dir);
		assert animation != IDLE;
	}

}
