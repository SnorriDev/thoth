package snorri.triggers;

import snorri.main.Main;
import snorri.windows.GameWindow;

public enum TriggerType {

	TIMELINE,
	BROADCAST,
	PRAY, //like broadcast, but callable by the player
	DOOR_OPEN,
	ACQUIRE,
	KILL,
	ENCHANT,
	HEAL,
	WRITE,
	DESTROY,
	TRIP,
	EXPLODE;
	
	/**
	 * Wrapper for activating triggers
	 * @return whether or not the triggers were fully loaded before activation
	 */
	public boolean activate(Object object) {
		
		if (!(Main.getWindow() instanceof GameWindow)) {
			return false;
		}
		
		TriggerMap map = ((GameWindow) Main.getWindow()).getWorld().getTriggerMap();
		if (map == null) {
			return true; //the world has no triggers
		}
		map.activate(this, object);
		return true;

	}
	
}