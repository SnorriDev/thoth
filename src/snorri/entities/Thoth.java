package snorri.entities;

import snorri.animations.Animation;
import snorri.world.Vector;

public class Thoth extends BossAIUnit {

	private static final long serialVersionUID = 1L;
	
	protected Thoth(Vector pos, Entity target, Animation idle, Animation walking) {
		super(pos, target, idle, walking);
	}
	
	
}
