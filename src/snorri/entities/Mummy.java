package snorri.entities;

import snorri.animations.Animation;
import snorri.world.Vector;

public class Mummy extends RangedEnemy {

	private static final long serialVersionUID = 1L;
	
	private static final Animation IDLE = new Animation("/textures/animations/mummy/idle");
	private static final Animation WALKING = new Animation("textures/animations/mummy/walking");
	
	public Mummy(Vector pos) {
		this(pos, null);
	}
	
	public Mummy(Vector pos, Entity target) {
		super(pos, target, IDLE, WALKING);
	}

}
