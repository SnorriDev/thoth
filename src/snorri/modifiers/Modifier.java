package snorri.modifiers;

import snorri.entities.Entity;
import snorri.inventory.Timer;

public abstract class Modifier<E extends Entity> {
	
	protected Timer timer;
	
	protected Modifier(double time) {
		timer = new Timer(time);
		timer.hardReset();
	}
	
	public boolean modifyAndCheckTimer(E e, double deltaTime) {
		timer.update(deltaTime);
		return timer.isOffCooldown();
	}
	
}
