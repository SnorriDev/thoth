package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.inventory.Orb;
import snorri.inventory.Weapon;
import snorri.main.Debug;
import snorri.main.Main;
import snorri.semantics.Go.Walker;
import snorri.world.Vector;
import snorri.world.World;

public class Projectile extends Detector implements Walker {

	private static final long serialVersionUID = 1L;

	private static final int PROJECTILE_SPEED = 450;
		
	private Vector velocity;
	private Entity root;
	
	private Weapon weapon;
	private Orb orb;
	
	public Projectile(Entity root, Vector rootVelocity, Vector path, Weapon weapon, Orb orb) {
		super(root.getPos().copy(), 3); //radius of a projectile is 1
		velocity = rootVelocity.copy().add(path.copy().scale(PROJECTILE_SPEED));
		this.animation = orb.getProjectileAnimation().getRotated(path.copy().normalize()); //new Animation(ANIMATION);
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
	
	public void applyForce(Vector force, double deltaTime) {
		velocity.add(force.copy().multiply(deltaTime));
	}
	
	@Override
	public void update(World world, double deltaTime) {
		
		if (weapon == null || !weapon.altersMovement()) {
			walk(world, velocity.copy().multiply(deltaTime));
		} 
		
		if (weapon != null) {
			Object output = weapon.useSpellOn(root, this, deltaTime / getLifeSpan());
			if (Debug.SHOW_WEAPON_OUTPUT) {
				Main.log("weapon output: " + output);
			}
		}
				
		//if we hit the edge of the map or a wall, end
		if (!world.canShootOver(pos)) {
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
		
		kill(e.getWorld());
		
	}
	
	@Override
	public void kill(World world) {
		
		if (killed) { //prevent infinite loops when the spell kills this object
			return;
		}
		
		super.kill(world);
		
		if (orb != null) {
			Object output = orb.useSpellOn(root, this); //
			if (Debug.SHOW_ORB_OUTPUT) {
				Main.log("orb output: " + output);
			}
		}
		
	}
	
	@Override
	public Object get(World world, AbstractSemantics attr) {
		
		if (attr == AbstractSemantics.SOURCE) {
			return root;
		}
		
		return super.get(world, attr);
		
	}

	@Override
	public void walk(World world, Vector delta) {
		pos.add(delta);
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		setDespawnable(true);
	}

}
