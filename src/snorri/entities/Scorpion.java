package snorri.entities;

import snorri.animations.Animation;

import snorri.world.Vector;

public class Scorpion extends AIUnit {

	private static final long serialVersionUID = 1L;

	public static final Animation IDLE = new Animation("/textures/animations/crocodile/idle");
	public static final Animation ATTACK = new Animation("/textures/animations/crocodile/attack");

	public Scorpion(Vector pos, Entity target) {
		super(pos, target, new Animation(IDLE), new Animation(ATTACK));
	}

	public Scorpion(Vector pos) {
		this(pos, null);
	}

	@Override
	protected int getAttackRange() {
		return 150;
	}

}
