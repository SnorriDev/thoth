package snorri.entities;

import snorri.animations.Animation;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.inventory.Orb;
import snorri.inventory.Weapon;
import snorri.main.Debug;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Ballista extends Entity {

	private static final long serialVersionUID = 1L;

	private static final Animation IDLE = new Animation("/textures/animations/ballista/idle");
	private static final Animation SHOOT = new Animation("/textures/animations/ballista/shoot");
	private static final double FIRE_DISTANCE = 1000d;
	private static final double SHOT_MARGIN = Math.PI / 4;

	private boolean shooting = false;
	private Entity target;
	private final Weapon bow = (Weapon) Item.newItem(ItemType.BOW);
	private final Orb bolt = (Orb) Item.newItem(ItemType.BOLT);

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

		if (target.getPos().distance(pos) < FIRE_DISTANCE //this logic should only apply in auto mode
				&& dir.getAngleBetween(target.getPos().copy().sub(pos)) < SHOT_MARGIN) {
			shoot(world);
		}
		bow.updateCooldown(d);

	}

	// TODO pass spell event, use shooter interface
	public boolean shoot(World world) {

		// Debug.raw("shooting: " + shooting);
		// Debug.raw("cannot use: " + !bow.canUse());

		if (shooting || !bow.canUse()) {
			return false;
		}

		// TODO timer
		// TODO Entities subclasses of each other?

		shooting = true;
		setAnimation(SHOOT);
		bow.attack(world, this, Vector.ZERO, dir, bolt);
		return true;

	}

	@Override
	protected void onCycleComplete(World world) {
		if (shooting) {
			Debug.raw("stopped shooting");
			setAnimation(IDLE);
			shooting = false;
		}
	}

}
