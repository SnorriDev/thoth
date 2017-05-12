package snorri.entities;

import snorri.animations.Animation;
import snorri.world.Vector;

public abstract class LandMeleeUnit extends Enemy {

	private static final long serialVersionUID = 1L;
	
	public LandMeleeUnit(Vector pos, Entity target, Animation walk, Animation attack) {
		super(pos, target, walk, attack);
		updateEntityToLatest();
	}
	
	public LandMeleeUnit(Vector pos, Entity target, Animation animation) {
		this(pos, target, animation, animation);
	}
	
	@Override
	public boolean updateEntityToLatest() {
		attackRange = 100;
		return true;
	}

}
