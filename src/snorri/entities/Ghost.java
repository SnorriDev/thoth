package snorri.entities;

import snorri.ai.AIMode;
import snorri.inventory.ManaManager;
import snorri.world.Vector;
import snorri.world.World;

public class Ghost extends BossAIUnit {
	
//	private static final Animation GHOST = new Animation("/textures/animations/ghost");

	private static final long serialVersionUID = 1L;
	
	public Ghost(Vector pos) {
		this(pos, null);
	}
	
	public Ghost(Vector pos, Entity target) {
		// TODO: Have animations here?
		super(pos, target, null, null);
		getInventory().add(new ManaManager());
	}

	@Override
	protected int getAttackRange() {
		return 400;
	}
	
	@Override
	public AIMode getDefaultMode() {
		return AIMode.FLY;
	}
	
	@Override
	public Vector getGravity() {
		// This is how we implement flying.
		return null;
	}
	
	@Override
	public void attack(Entity target, World world) {
		// If the ghost has a spell and we can cast, use it.
		if (getInventory().getPapyrus() != null && getInventory().getPapyrus().canCast()) {
			getInventory().cast(world, target.getPos());
		}
		
		// If the ghost has a weapon, attack.
		if (getInventory().getWeapon() != null) {
			Vector direction = target.getPos().sub(getPos()).normalize();
			getInventory().attack(world, Vector.ZERO, direction);	
		}
	}
	
}
