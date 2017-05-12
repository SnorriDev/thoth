package snorri.entities;

import snorri.animations.Animation;
import snorri.world.Vector;

/**
 * This class can be used to set pathfinding behavior for melee/short-ranged units.
 * Note that the enemy will use whatever weapon is in the superclass's inventory.
 * This class has more efficient movement logic for close-range units.
 * @author snorri
 *
 */
public abstract class CloseRangeAIUnit extends AIUnit {

	private static final long serialVersionUID = 1L;
	
	//TODO figure out a better way to structure this class hierarchy
	
	public CloseRangeAIUnit(Vector pos, Entity target, Animation walk, Animation attack) {
		super(pos, target, walk, attack);
	}
	
	public CloseRangeAIUnit(Vector pos, Entity target, Animation animation) {
		this(pos, target, animation, animation);
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		attackRange = 100;
	}

}
