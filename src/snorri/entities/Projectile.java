package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.events.CastEvent;
import snorri.events.CastEvent.Caster;
import snorri.inventory.Orb;
import snorri.inventory.Weapon;
import snorri.main.Debug;
import snorri.semantics.commands.intrans.Go.Movable;
import snorri.semantics.nouns.Nominal;
import snorri.world.Vector;
import snorri.world.World;

public class Projectile extends Detector implements Movable {

	private static final long serialVersionUID = 1L;

	private static final int PROJECTILE_SPEED = 450;

	private Entity root;
	
	private Weapon weapon;
	private Orb orb;
	
	public Projectile(Entity root, Vector rootVelocity, Vector path, Weapon weapon, Orb orb) {
		super(root.getPos().copy(), 3); //radius of a projectile is 1
		velocity = rootVelocity.add(path.scale(PROJECTILE_SPEED));
		this.animation = orb.getProjectileAnimation();
//		setDirection(path);
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
		velocity.add_(force.multiply(deltaTime));
	}
	
	@Override
	public void update(World world, double deltaTime) {
		
		if (weapon == null || weapon.getSpell() == null) {
			translate(world, velocity.multiply(deltaTime));
		}
		else if (root instanceof Caster) {
			CastEvent spellEvent = new CastEvent(world, (Caster) root, this, deltaTime / getLifeSpan());
			weapon.onCast(spellEvent);
			if (!weapon.getSpell().altersMovement()) {
				translate(world, velocity.multiply(deltaTime));
			}
			// Maybe we should always apply physics, for interesting interactions?
		}
				
		super.update(world, deltaTime);
	}
	
	@Override
	protected void updatePosition(World world, double deltaTime) {
		if (!world.canShootOver(pos)) {
			world.delete(this);
		}
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
			CastEvent castEvent = new CastEvent(world, (Caster) root, this);
			Object output = orb.onCast(castEvent);
			if (Debug.orbOutputLogged()) {
				Debug.logger.info("Orb output: " + output + ".");
			}
		}		
	}
	
	@Override
	public Nominal get(AbstractSemantics attr, CastEvent e) {
		if (attr == AbstractSemantics.SOURCE) {
			return root;
		}
		return super.get(attr, e);
	}

	@Override
	public void translate(World world, Vector delta) {
		pos = pos.add(delta);
	}
	
	@Override
	public boolean isFalling() {
		return this.getVelocity().magnitude() < Unit.getTerminalVelocity();
	}
	
	@Override
	public void refreshStats() {
		super.refreshStats();
		ignoreCollisions = true;
		setDespawnable(true);
	}
	
	@Override
	public boolean hasGravity() {
		return true;
	}

}
