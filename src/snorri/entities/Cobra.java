package snorri.entities;

import snorri.animations.Animation;
import snorri.inventory.Inventory;
import snorri.world.Vector;

public class Cobra extends LandMeleeUnit {

	private static final long serialVersionUID = 1L;
	
	public static final Animation IDLE = new Animation("/textures/animations/cobra");
	
	public Cobra(Vector pos, Entity target) {
		super(pos, target, new Animation(IDLE));
		this.target = target;
		animation = new Animation(IDLE);
		inventory = new Inventory(this);
	}
	
	public Cobra(Vector pos) {
		super(pos, null);
		animation = new Animation(IDLE);
	}
	
}
