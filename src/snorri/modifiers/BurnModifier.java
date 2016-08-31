package snorri.modifiers;

import snorri.entities.Unit;

public class BurnModifier extends Modifier<Unit> {

	protected static final double BURN_DOT = 8d;

	public BurnModifier() {
		super(DEFAULT_TIME);
	}
	
	@Override
	public boolean modify(Unit u, double deltaTime) {
		u.damage(BURN_DOT * deltaTime);
		return super.modify(u, deltaTime);
	}

	
}
