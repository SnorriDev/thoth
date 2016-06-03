package snorri.inventory;

public class CooldownTimer {

	private float currentTime, cooldown;
	
	public CooldownTimer(int cooldown) {
		this.cooldown = cooldown;
		currentTime = cooldown;
	}
	
	public void update(float deltaTime) {
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
	
	public boolean isOffCooldown() {
		return currentTime >= cooldown;
	}
	
	public int getRatio(int max) {
		return (int) ((cooldown - currentTime) / cooldown * max);
	}
	
	public float getCooldown() {
		if (isOffCooldown()) {
			return 0;
		}
		return cooldown - currentTime;
	}
	
}
