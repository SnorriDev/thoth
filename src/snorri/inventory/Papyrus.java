package snorri.inventory;

import java.awt.Color;

import snorri.entities.Unit;
import snorri.main.Main;

public class Papyrus extends Item {

	private static final long serialVersionUID = 1L;
	
	private static final Color PAPYRUS_COOLDOWN_COLOR = new Color(1, 69, 101, 150);
	
	public Papyrus(ItemType t) {
		super(t);
		timer = new Timer(5);
	}
	
	public boolean tryToActivate(Unit player) {
		
		if (timer.activate()) {
			Object o = useSpellOn(player);
			Main.log("spell output: " + o);
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public Color getArcColor() {
		return PAPYRUS_COOLDOWN_COLOR;
	}
	
	@Override
	public int getInvPos() {
		return 3;
	}

}
