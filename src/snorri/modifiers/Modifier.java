package snorri.modifiers;

import snorri.entities.Entity;
import snorri.inventory.Timer;

public abstract class Modifier<E extends Entity> {
	
	protected Timer timer;
	protected static final double DEFAULT_TIME = 10d;
	
	protected Modifier(double time) {
		timer = new Timer(time);
		timer.hardReset();
	}
	
	public boolean modify(E e, double deltaTime) {
		timer.update(deltaTime);
		return timer.isOffCooldown();
	}
	
}
