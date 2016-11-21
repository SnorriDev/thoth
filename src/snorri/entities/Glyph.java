package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.events.CollisionEvent;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.World;

/**
 * Action buttons that should be used primarily as teleporters
 * @author snorri
 */

public class Glyph extends Detector {

	private static final long serialVersionUID = 1L;

	private static final Animation IDLE = new Animation("/textures/animations/glyph/idle");
	private static final Animation ACTIVATE = new Animation("/textures/animations/glyph/activate");
	
	private boolean activated = false;
	
	public Glyph(Vector pos) {
		super(pos, new RectCollider(26, 45));
		treeMember = true;
		age = -1;
		ignoreCollisions = true;
		animation = new Animation(IDLE);
		z = DEFAULT_LAYER;
	}

	@Override
	public void onCollision(CollisionEvent e) {
		
		if (!activated && (e.getTarget() instanceof Player || e.getTarget() instanceof Explosion)) {
			activate();
		}
		
	}
	
	@Override
	public void update(World world, double deltaTime) {
		if (activated && animation.hasCycled()) {
			world.delete(this);
		}
		super.update(world, deltaTime);
	}
	
	public void activate() {
		animation = new Animation(ACTIVATE);
		activated = true;
		TriggerType.BROADCAST.activate(tag);
	}

}
