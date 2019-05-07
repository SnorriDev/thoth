package snorri.inventory;

import snorri.audio.Audio;
import snorri.audio.ClipWrapper;
import snorri.entities.Entity;
import snorri.entities.Projectile;
import snorri.main.Debug;
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
	
	public boolean attackIfPossible(World world, Entity focus, Vector movement, Vector dir, Orb orb) {
		if (timer.activate()) {
			Debug.logger.fine("Inside timer brackets");
			Audio.playClip(clip);
			world.add(new Projectile(focus, movement, dir, this, orb));
			Debug.logger.fine("Spawned new projectile.");
			return true;
		}
		return false;
	}

}
