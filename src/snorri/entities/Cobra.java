package snorri.entities;

import snorri.animations.Animation;
import snorri.world.Vector;

public class Cobra extends LandMeleeUnit {

	private static final long serialVersionUID = 1L;
	
	public static final Animation IDLE = new Animation("/textures/animations/cobra");
	
	public Cobra(Vector pos, Entity target) {
		super(pos, target, IDLE);
	}
	
	public Cobra(Vector pos) {
		this(pos, null);
	}
	
}
