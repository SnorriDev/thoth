package snorri.entities;

import snorri.animations.Animation;
import snorri.world.Vector;
import snorri.world.World;

public abstract class BossAIUnit extends AIUnit {

	private static final long serialVersionUID = 1L;

	protected BossAIUnit(Vector pos, Entity target, Animation idle, Animation walking) {
		super(pos, target, idle, walking);
	}
	
	@Override
	public void attack(World world, Entity e) {
		if (inventory == null) {
			return;
		}
		//TODO add some complex logic here
		//Probably want to just override this function for every boss type
		inventory.getPapyrus(0).tryToActivate(e);
	}
	
}
