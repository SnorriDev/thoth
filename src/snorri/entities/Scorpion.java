package snorri.entities;

import snorri.animations.Animation;
import snorri.inventory.Inventory;

import snorri.world.Vector;

public class Scorpion extends CloseRangeAIUnit {

	private static final long serialVersionUID = 1L;

	public static final Animation IDLE = new Animation("/textures/animations/crocodile/idle");
	public static final Animation ATTACK = new Animation("/textures/animations/crocodile/attack");

	protected double seekRange = 128;
	protected double attackPower = 2; //TODO: change to something more logical and balanced

	protected Inventory inventory;
	protected Entity target;

	public Scorpion(Vector pos, Entity target) {
		super(pos, target, new Animation(IDLE), new Animation(ATTACK));
		inventory = new Inventory(this);
	}

	public Scorpion(Vector pos) {
		this(pos, null);
		animation = new Animation(IDLE);
	}

}
