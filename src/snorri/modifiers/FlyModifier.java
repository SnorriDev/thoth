package snorri.modifiers;

import snorri.entities.Unit;
import snorri.world.Vector;

public class FlyModifier extends Modifier<Unit> {
	
	protected static final double DEFAULT_TIME = 10d;
	protected static final Vector FLYING_GRAVITY = new Vector(0, 100);
	
	public FlyModifier() {
		super(DEFAULT_TIME);
	}

	@Override
	public boolean modifyAndCheckTimer(Unit u, double deltaTime) {
		boolean timedOut = super.modifyAndCheckTimer(u, deltaTime);
		if (timedOut) {
			u.setCustomGravity(null);
		} else {
			u.setCustomGravity(FLYING_GRAVITY);
		}
		return timedOut;
	}

}
