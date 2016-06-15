package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.inventory.Orb;
import snorri.inventory.Weapon;
import snorri.main.Debug;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Projectile extends Collider {

	private static final long serialVersionUID = 1L;

	private static final int PROJECTILE_SPEED = 175;
		
	private Vector velocity;
	private Entity root;
	
	private Weapon weapon;
	private Orb orb;
	
	public Projectile(Entity root, Vector rootVelocity, Vector path, Weapon weapon, Orb orb) {
		super(root.getPos().copy(), 1); //radius of a projectile is 1
		velocity = rootVelocity.copy().add(path.copy().scale(PROJECTILE_SPEED));
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
	public void update(World world, double deltaTime) {
		
		if (weapon == null || !weapon.altersMovement()) {
			pos.add(velocity.copy().multiply(deltaTime));
		} 
		
		if (Debug.SHOW_WEAPON_OUTPUT && weapon != null) {
			Main.log("weapon output: " + weapon.useSpellOn(this, deltaTime / getLifeSpan()));
		}
				
		//if we hit the edge of the map or a wall, end
		if (world.getLevel().getTile(pos) == null || ! world.getLevel().getTile(pos).isPathable()) {
			//TODO: activate spell?
			world.delete(this);
		}
				
		super.update(world, deltaTime);
	}

	@Override
	public void onCollision(CollisionEvent e) {
		
		if (root.equals(e.getTarget())) {
			return;
		}
				
		if (e.getTarget() instanceof Unit) {
			((Unit) e.getTarget()).damage(weapon.getSharpness());
		}
		
		if (orb != null) {
			Main.log("orb output: " + orb.useSpellOn(e.getTarget()));
		}
				
		e.getWorld().delete(this); //could use removeFrom, but this is a little better
		
	}
	
	@Override
	public Object get(World world, AbstractSemantics attr) {
		
		if (attr == AbstractSemantics.SOURCE) {
			return root;
		}
		
		return super.get(world, attr);
		
	}

}
