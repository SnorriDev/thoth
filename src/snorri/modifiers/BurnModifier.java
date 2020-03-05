package snorri.modifiers;

import snorri.entities.Unit;

public class BurnModifier extends Modifier<Unit> {

	protected static final double DEFAULT_TIME = 5d;
	protected static final double BURN_DOT = 10d;

	public BurnModifier() {
		super(DEFAULT_TIME);
	}
	
	@Override
	public boolean modifyAndCheckTimer(Unit u, double deltaTime) {
		u.damage(BURN_DOT * deltaTime);
		return super.modifyAndCheckTimer(u, deltaTime);
	}

	
}
