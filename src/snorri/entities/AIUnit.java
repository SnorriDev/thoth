package snorri.entities;

import snorri.ai.AIAgent;
import snorri.ai.AIMode;
import snorri.animations.Animation;
import snorri.collisions.Collider;
import snorri.world.Vector;
import snorri.world.World;

public abstract class AIUnit extends Unit implements AIAgent {
	
	private static final long serialVersionUID = 1L;
	
	private Entity target;
	private AIMode mode;
	
	protected AIUnit(Vector pos, Entity target, Animation idle, Animation walking) {
		super(pos, idle, walking);
		setTarget(target);
		mode = getDefaultMode();
	}
	
	protected AIUnit(Vector pos, Entity target, Collider collider, Animation walk, Animation attack) {
		super(pos, collider, walk, attack);
		setTarget(target);
		mode = getDefaultMode();
	}
	
	protected abstract int getAttackRange();
	
	@Override
	public void update(World world, double deltaTime) {
		if (getTarget() == null) {
			setTarget(world.computeFocus());
		}
		
		// Call the generic update logic for Units.
		super.update(world, deltaTime);
		
		// Let the AI act according to its AIMode.
		mode.update(this, world, deltaTime);
	}
	
	/** Return whether the AIUnit can currently attack.
	 * 
	 * If the AIUnit has no weapon, this method returns true if it is in attack range.
	 * If the AIUnit does have a weapon, it also considers whether the weapon is on cooldown.
	 */
	@Override
	public boolean canAttack(Entity target, World world) {
		// Note that this defaults to true if there is no weapon (=> can attack without a weapon).
		boolean canUseWeapon = true;
		// If there is a weapon, make sure it is off cooldown.
		if (getInventory() != null && getInventory().getWeapon() != null) {
			canUseWeapon = getInventory().getWeapon().canUse();
		}
		int attackRange = getAttackRange();
		return canUseWeapon && target.getPos().distanceSquared(getPos()) < attackRange * attackRange;
	}
	
	@Override
	public void attack(Entity target, World world) {
		getInventory().attack(world, Vector.ZERO.copy(), target.getPos().copy().sub_(pos));
	}
	
	@Override
	public final void setTarget(Entity target) {
		this.target = target;
	}
	
	@Override
	public final Entity getTarget() {
		return target;
	}
	
	public final void setMode(AIMode mode) {
		this.mode = mode;
	}
	
	public final AIMode getMode() {
		return mode;
	}
	
	public AIMode getDefaultMode() {
		return AIMode.TURRET;
	}

}
