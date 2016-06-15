package snorri.inventory;

import java.io.Serializable;

public class Timer implements Serializable {

	/**
	 * Used for timing cooldowns and burning
	 * TODO: change this stuff to doubles
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
		
		currentTime = 0f;
		return true;
		
	}
	
	public void hardReset() {
		currentTime = 0f;
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
	
}
