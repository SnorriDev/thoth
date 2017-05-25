package snorri.entities;

import snorri.animations.Animation;
import snorri.events.SpellEvent.Caster;
import snorri.world.Vector;
import snorri.world.World;

public abstract class BossAIUnit extends AIUnit implements Caster {

	private static final long serialVersionUID = 1L;

	public BossAIUnit(Vector pos) {
		this(pos, null, null, null);
	}
	
	protected BossAIUnit(Vector pos, Entity target, Animation idle, Animation walking) {
		super(pos, target, idle, walking);
	}
	
	// should override these methods for more complex behavior
	
	@Override
	public boolean canAttack(Entity target, World world) {
		if (inventory == null || inventory.getPapyrus(0) == null) {
			return false;
		}
		return target.pos.distanceSquared(pos) < attackRange * attackRange && inventory.getPapyrus(0).canUse();
	}
	
	@Override
	public void attack(World world, Entity e) {
		inventory.getPapyrus(0).tryToActivate(world, this, e);
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		attackRange = 900;
	}
	
	@Override
	public Vector getAimPosition() {
		if (target == null) {
			return pos.copy();
		}
		return target.pos.copy();
	}
	
}
