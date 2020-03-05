package snorri.modifiers;

import snorri.entities.Unit;

public class SlowModifier extends Modifier<Unit> {
	
	protected static final double DEFAULT_TIME = 10d;
	protected static final double DEFAULT_MAGNITUDE = 0.5;
	
	protected double magnitude;
	
	
	public SlowModifier() {
		super(DEFAULT_TIME);
		magnitude = DEFAULT_MAGNITUDE;
	}
	
	public SlowModifier(double time) {
		super(time);
		magnitude = DEFAULT_MAGNITUDE;
	}
	
	public SlowModifier(double time, double factor) {
		super(time);
		magnitude = factor;
	}

	@Override
	public boolean modifyAndCheckTimer(Unit u, double deltaTime) {
		u.modifySpeed(magnitude);
		return super.modifyAndCheckTimer(u, deltaTime);
	}

}
