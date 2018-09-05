package snorri.entities;

import snorri.ai.AIAgent;
import snorri.animations.Animation;
import snorri.collisions.Collider;
import snorri.inventory.Carrier;
import snorri.world.Vector;
import snorri.world.World;

/**
 * This class can be used to set pathfinding behavior for melee/short-ranged units.
 * Note that the enemy will use whatever weapon is in the superclass's inventory.
 * This class has more efficient movement logic for close-range units.
 * @author snorri
 *
 */
public abstract class CloseRangeAIUnit extends Unit implements AIAgent, Carrier {

	private static final long serialVersionUID = 1L;
	
	private int attackRange;
	private int stopRange;
	
	public CloseRangeAIUnit(Vector pos, Entity target, Animation walk, Animation attack) {
		super(pos, walk, attack);
		setTarget(target);
	}
	
	public CloseRangeAIUnit(Vector pos, Entity target, Collider collider, Animation walk, Animation attack) {
		super(pos, collider, walk, attack);
		setTarget(target);
	}
	
	public CloseRangeAIUnit(Vector pos, Entity target, Animation animation) {
		this(pos, target, animation, animation);
	}
	
	@Override
	public boolean canAttack(Entity target, World world) {
		if (getInventory() == null || getInventory().getWeapon() == null) {
			return false;
		}
		return target.pos.distanceSquared(pos) < attackRange * attackRange && getInventory().getWeapon().canUse();
	}
	
	// TODO(lambdaviking): Bring back AIUnit?
	
	@Override
	public void attack(Entity target, World world) {
		getInventory().attack(world, Vector.ZERO.copy(), target.getPos().copy().sub_(pos));
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		attackRange = 100;
		stopRange = 80;
	}

}
