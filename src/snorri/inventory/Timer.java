package snorri.inventory;

public class Timer {

	private float currentTime, cooldown;
	
	public Timer(float cooldown) {
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
	
	public void hardReset() {
		currentTime = 0f;
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
