package snorri.entities;

import snorri.animations.Animation;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.inventory.Weapon;
import snorri.main.Main;
import snorri.windows.FocusedWindow;
import snorri.world.Vector;
import snorri.world.World;

public class Ballista extends Entity {

	private static final long serialVersionUID = 1L;

	private static final Animation IDLE = new Animation("/textures/animations/ballista/idle");
	private static final Animation SHOOT = new Animation("/textures/animations/ballista/shoot");
	private static final double FIRE_DISTANCE = 1000d;
	private static final double SHOT_MARGIN = Math.PI / 4;

	private boolean shooting = false, active = false;
	private Entity target;
	private final Weapon bow = (Weapon) Item.newItem(ItemType.BOW);

	public Ballista(Vector pos, Vector dir) {
		super(pos, 35);
		setAnimation(IDLE);
		setDirection(dir);
	}

	@Override
	public void update(World world, double d) {
		super.update(world, d);
		if (target == null) {
			target = ((FocusedWindow<?>) Main.getWindow()).getFocus();
		}
		
		bow.updateCooldown(d);
		if (!active) {
			return;
		}

		if (target.getPos().distance(pos) < FIRE_DISTANCE //this logic should only apply in auto mode
				&& dir.getAngleTo(target.getPos().copy().sub_(pos)) < SHOT_MARGIN) {
			shoot(world);
		}

	}

	// TODO(#42): Refactor to use TURRET AILogic.
	public boolean shoot(World world) {
		if (shooting || !bow.canUse()) {
			return false;
		}

		shooting = true;
		setAnimation(SHOOT);
		bow.attackIfPossible(world, this, Vector.ZERO, dir);
		return true;
	}

	@Override
	protected void onCycleComplete(World world) {
		if (shooting) {
			setAnimation(IDLE);
			shooting = false;
		}
	}
	
	public void activate() {
		active = true;
	}
	
	public void deactivate() {
		active = false;
	}
	
	@Override
	public Vector getGravity() {
		return null;
	}

}
