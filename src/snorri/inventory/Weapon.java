package snorri.inventory;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.main.GameWindow;
import snorri.main.Main;

public class Weapon extends Item {

	private static final long serialVersionUID = 1L;

	public Weapon(ItemType t) {
		super(t);
		timer = new Timer(0.3f);
	}
	
	//retrieve the sharpness for this weapon from the ItemType
	public int getSharpness() {
		return (int) type.getProperty(0);
	}
	
	public void setCustomTimer(Timer timer) {
		this.timer = timer;
	}

	public boolean altersMovement() {
		
		if (spell == null) {
			return false;
		}
		
		return spell.altersMovement();
	}
	
	public Object useSpellOn(Entity subject, double modifier) {
		
		if (spell == null) {
			return null;
		}
				
		SpellEvent e = new SpellEvent((GameWindow) Main.getWindow(), subject, modifier);
		return spell.getMeaning(e);
		
	}

}
