package snorri.entities;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.events.CollisionEvent;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;

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
		
		if (activated && animation.hasCycled()) {
			e.getWorld().delete(this);
		}
		
		if (!activated && (e.getTarget() instanceof Player || e.getTarget() instanceof Explosion)) {
			activate();
		}
		
	}
	
	public void activate() {
		animation = new Animation(ACTIVATE);
		TriggerType.BROADCAST.activate(tag);
		activated = true;
	}

}
