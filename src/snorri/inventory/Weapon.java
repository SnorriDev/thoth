package snorri.inventory;

import snorri.audio.Audio;
import snorri.audio.ClipWrapper;
import snorri.entities.Entity;
import snorri.entities.Projectile;
import snorri.events.SpellEvent;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Weapon extends Item {

	private static final long serialVersionUID = 1L;

	private ClipWrapper clip;
	
	public Weapon(ItemType t) {
		super(t);
		timer = new Timer(getBaseCooldown());
		clip = new ClipWrapper((String) type.getProperty(2));
	}
	
	//retrieve the sharpness for this weapon from the ItemType
	public double getSharpness() {
		return (double) type.getProperty(0);
	}
	
	public double getBaseCooldown() {
		return (double) type.getProperty(1);
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
	
	@Override
	public int getInvPos() {
		return 0;
	}
	
	boolean attack(World world, Entity focus, Vector movement, Vector dir, Orb orb) {
		if (timer.activate()) {
			Audio.playClip(clip);
			world.add(new Projectile(focus, movement, dir, this, orb));
			return true;
		}
		return false;
	}

}
