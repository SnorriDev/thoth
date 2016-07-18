package snorri.inventory;

import java.io.Serializable;

public class Timer implements Serializable {

	/**
	 * used for timing cooldowns and burning
	 */
	
	private static final long serialVersionUID = 1L;
	private double currentTime, cooldown;
	
	public Timer(double cooldown) {
		this.cooldown = cooldown;
		currentTime = cooldown;
	}
	
	public void update(double deltaTime) {
		if (! isOffCooldown()) {
			currentTime += deltaTime;
		}
	}
	
	public boolean activate() {
		
		if (! isOffCooldown()) {
			return false;
		}
		
		currentTime = 0d;
		return true;
		
	}
	
	public void hardReset() {
		currentTime = 0d;
	}
	
	public boolean isOffCooldown() {
		return currentTime >= cooldown;
	}
	
	public int getRatio(int max) {
		return (int) ((cooldown - currentTime) / cooldown * max);
	}
	
	public double getCooldown() {
		if (isOffCooldown()) {
			return 0;
		}
		return cooldown - currentTime;
	}

	@Deprecated
	public void setDelay(double attackDelay) {
		cooldown = attackDelay;
		currentTime = cooldown;
	}

	public Object getTime() {
		return currentTime;
	}
	
}
