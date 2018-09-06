package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.events.SpellEvent;
import snorri.events.SpellEvent.Caster;
import snorri.inventory.Orb;
import snorri.inventory.Weapon;
import snorri.main.Debug;
import snorri.semantics.Go.Movable;
import snorri.semantics.Nominal;
import snorri.world.Vector;
import snorri.world.World;

public class Projectile extends Detector implements Movable {

	private static final long serialVersionUID = 1L;

	private static final int PROJECTILE_SPEED = 450;
		
	private Vector velocity;
	private Entity root;
	
	private Weapon weapon;
	private Orb orb;
	
	public Projectile(Entity root, Vector rootVelocity, Vector path, Weapon weapon, Orb orb) {
		super(root.getPos().copy(), 3); //radius of a projectile is 1
		velocity = rootVelocity.copy().add_(path.copy().scale_(PROJECTILE_SPEED));
		this.animation = orb.getProjectileAnimation();
		setDirection(path);
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
		velocity.add_(force.copy().multiply_(deltaTime));
	}
	
	@Override
	public void update(World world, double deltaTime) {
		
		boolean walked = false;
		if (weapon == null || !weapon.altersMovement()) {
			translate(world, velocity.copy().multiply_(deltaTime));
			walked = true;
		} 
		
		if (root instanceof Caster && weapon != null) {
			
			Object output = weapon.useSpellOn(world, ((Caster) root), this, deltaTime / getLifeSpan());
			if (Debug.weaponOutputLogged()) {
				Debug.logger.info("Weapon output: " + output + ".");
			}
			
			//we can't unify this with the above if clause because it matters when spells are applied
			if (!walked && output.equals(false)) {
				translate(world, velocity.copy().multiply_(deltaTime));
			}
			
		}
				
		//if we hit the edge of the map or a wall, end
		if (!world.canShootOver(pos)) {
			world.delete(this);
		}
		// FIXME why isn't this working off grid?
		
		// Potential Hazard: you can slow down your falling by moving left or right
		if(this.isFlying() == false && this.isFalling() == true && this.getVelocity().magnitude() < Projectile.getTerminalVelocity()) {
			this.addVelocity(new Vector(0, -512.0 * deltaTime));
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
		
		e.getWorld().delete(this);
		
	}
	
	@Override
	protected void onSafeDelete(World world) {
		if (root instanceof Caster && orb != null) {
			Object output = orb.useSpellOn(world, (Caster) root, this);
			if (Debug.orbOutputLogged()) {
				Debug.logger.info("Orb output: " + output + ".");
			}
		}		
	}
	
	@Override
	public Nominal get(AbstractSemantics attr, SpellEvent e) {
		
		if (attr == AbstractSemantics.SOURCE) {
			return root;
		}
		
		return super.get(attr, e);
		
	}

	@Override
	public void translate(World world, Vector delta) {
		pos.add_(delta);
	}
	
	@Override
	public boolean isFalling() {
		return true;
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		setDespawnable(true);
	}

}
