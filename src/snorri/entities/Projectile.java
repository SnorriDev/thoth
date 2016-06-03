package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.inventory.Orb;
import snorri.inventory.Weapon;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Projectile extends Collider {

	private static final long serialVersionUID = 1L;

	private static final int PROJECTILE_SPEED = 150;
		
	private Vector velocity;
	private Entity root;
	
	private Weapon weapon;
	private Orb orb;
	
	public Projectile(Entity root, Vector rootVelocity, Vector path, Weapon weapon, Orb orb) {
		super(root.getPos().copy(), 1); //radius of a projectile is 1
		velocity = rootVelocity.copy().add(path.copy().multiply(PROJECTILE_SPEED));
		this.root = root;
		this.weapon = weapon;
		this.orb = orb;
	}

	public Entity getRoot() {
		return root;
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
	
	public Orb getOrb() {
		return orb;
	}
	
	@Override
	public void update(World world, float deltaTime) {
		pos.add(velocity.copy().multiply(deltaTime));
		super.update(world, deltaTime);
	}

	@Override
	public void onCollision(CollisionEvent e) {
		
		if (e.getTarget().equals(root)) {
			return;
		}
		
		if (e.getTarget() instanceof Unit) {
			((Unit) e.getTarget()).damage(weapon.getSharpness());
		}
		
		if (orb != null) {
			Main.log("weapon output: " + orb.useSpellAt(e.getTarget()));
		}
				
		e.getWorld().delete(this); //could use removeFrom, but this is a little better
		
	}

}
