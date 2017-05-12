package snorri.entities;

import snorri.animations.Animation;
import snorri.world.Vector;
import snorri.world.World;

public abstract class RangedEnemy extends Enemy {

	private static final long serialVersionUID = 1L;
	
	protected RangedEnemy(Vector pos, Entity target, Animation idle, Animation walking) {
		super(pos, target, idle, walking);
	}

	/**
	 * does a grid raycast to detect unpathable terrain blocking the shot
	 * @param target
	 * 		the entity we're tryna cap
	 * @return
	 * 		whether terrain obstructs the shot
	 */
	@Override
	public boolean canAttack(Entity target, World world) {
		
		// do this cheap check first for efficiency
		if (!super.canAttack(target, world)) {
			return false;
		}
		
		Vector step = target.pos.copy().sub(pos).normalize();
		Vector tempPos = pos.copy();
		
		//I'm checking if pos and target.pos are both okay just in case we're in a wall
		while (tempPos.distanceSquared(pos) <= target.pos.distanceSquared(pos)) {	
		
			if (! world.canShootOver(tempPos)) {
				return false;
			}
			
			if (tempPos.distanceSquared(pos) > attackRange * attackRange) {
				return false;
			}
			
			tempPos.add(step);	
		}

		return true;
		
	}
	
}
