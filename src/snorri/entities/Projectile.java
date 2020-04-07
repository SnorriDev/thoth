package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.animations.Animation;
import snorri.events.CastEvent;
import snorri.events.CastEvent.Caster;
import snorri.inventory.Weapon;
import snorri.main.Debug;
import snorri.main.Main;
import snorri.semantics.commands.intrans.Go.Movable;
import snorri.semantics.nouns.Nominal;
import snorri.world.Vector;
import snorri.world.World;

public class Projectile extends Detector implements Movable {

	private static final long serialVersionUID = 1L;

	// TODO: Move this to be a property of the weapon.
	private Animation PLACEHOLDER = new Animation(Main.getImage("/textures/objects/pellet.png"));
	private static final int PROJECTILE_SPEED = 450;

	private Entity root;
	
	private Weapon weapon;
	private boolean movementOverriden;
	
	public Projectile(Entity root, Vector rootVelocity, Vector path, Weapon weapon) {
		super(root.getPos().copy(), 3); //radius of a projectile is 1
		velocity = rootVelocity.add(path.scale(PROJECTILE_SPEED));
		// TODO: Should attach this to the weapon type.
		this.animation = new Animation(PLACEHOLDER);
//		setDirection(path);
		this.root = root;
		this.weapon = weapon;
		movementOverriden = false;
	}

	public Entity getRoot() {
		return root;
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
	
	public void applyForce(Vector force, double deltaTime) {
		velocity.add_(force.multiply(deltaTime));
	}
	
	@Override
	public void update(World world, double deltaTime) {		
		if (weapon == null || !(root instanceof Caster) || weapon.getSpell() == null) {
			translate(world, velocity.multiply(deltaTime));
		}
		else {
			CastEvent spellEvent = new CastEvent(world, (Caster) root, this, deltaTime / getLifeSpan());
			weapon.wrapCastSpell(spellEvent);
			if (!movementOverriden) {
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
		if (root instanceof Caster && weapon != null) {
			CastEvent castEvent = new CastEvent(world, (Caster) root, this);
			Object output = weapon.wrapCastSpell(castEvent);
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
	public Vector getGravity() {
		return Entity.GRAVITY;
	}

	public void setMovementOverriden(boolean overriden) {
		movementOverriden = overriden;
	}

}
