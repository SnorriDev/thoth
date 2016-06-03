package snorri.inventory;

import java.awt.Image;

import snorri.entities.Player;
import snorri.main.Main;

public class Papyrus extends Item {

	private CooldownTimer timer;
	
	public Papyrus(ItemType t) {
		super(t);
		timer = new CooldownTimer(5);
	}
	
	public void updateCooldown(float deltaTime) {
		timer.update(deltaTime);
	}
	
	public boolean canUse() {
		return timer.isOffCooldown();
	}
	
	public CooldownTimer getTimer() {
		return timer;
	}
	
	public boolean tryToActivate(Player player) {
		
		if (timer.activate()) {
			Main.log("spell output: " + useSpellAt(player));
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public Image getBorder(boolean selected) {
		return selected ? PAPYRUS_BORDER_SELECTED : PAPYRUS_BORDER;
	}

}
