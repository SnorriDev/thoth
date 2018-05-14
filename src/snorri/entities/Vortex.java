package snorri.entities;

import snorri.animations.Animation;
import snorri.events.CollisionEvent;
import snorri.world.Vector;

public class Vortex extends Detector {

	private static final long serialVersionUID = 1L;
	private static final Animation VORTEX_ANIMATION = new Animation("/textures/animations/vortex");
	
	protected static final double FIELD_STRENGTH = 50000000d;
	protected static final double ENTITY_SCALE = 1 / 1000d;
	protected static final double EVENT_HORIZON = 30;
	
	public Vortex(Vector pos) {
		super(pos, 500);
	}
	
	public Vector getForce(Entity e) {
		if (pos.copy().sub_(e.pos).magnitude() <= EVENT_HORIZON) {
			return Vector.ZERO.copy();
		}
		double coeff = FIELD_STRENGTH / (e.pos.distance(pos) * e.pos.distance(pos) * e.pos.distance(pos));
		return pos.copy().sub_(e.pos).multiply_(coeff); //attractive force
	}

	@Override
	public void onCollision(CollisionEvent e) {
		
		//issue when trying to place this too close to the edge of the map
		
		if (e.getTarget().isStaticObject()) {
			return;
		}
		
		if (e.getTarget() instanceof Projectile) {
			Vector force = getForce(e.getTarget());
			((Projectile) e.getTarget()).applyForce(force, e.getDeltaTime());
			if (force.equals(Vector.ZERO)) {
				e.getWorld().delete(e.getTarget());
			}
			return;
		}
		
		Vector newPos = getForce(e.getTarget()).multiply_(ENTITY_SCALE).add_(e.getTarget().pos);
		e.getWorld().getEntityTree().move(e.getTarget(), newPos);
		
	}
	
	@Override
	public void updateEntityStats() {
		super.updateEntityStats();
		animation = new Animation(VORTEX_ANIMATION);
		this.ignoreCollisions = true;
		this.hitAll = true;
	}

}
