package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.triggers.Trigger;
import snorri.world.Vector;

public class Listener extends Detector {

	/**
	 * TODO different types of listeners for different entities
	 */
	private static final long serialVersionUID = 1L;
	
	private Entity target;

	public Listener(Vector pos, int r, String msg) {
		super(pos, r);
		this.msg = msg;
		treeMember = true;
		age = -1;
		ignoreCollisions = true;
	}
	
	public Listener(Vector pos, int r, String msg, Entity e) {
		this(pos, r, msg);
		target = e;
	}

	@Override
	public void onCollision(CollisionEvent e) {

		if (msg != null && (target == e.getTarget() || (target == null && e.getTarget() instanceof Player))) {
			Trigger.TriggerType.BROADCAST.activate(msg);
		}

	}
	
}
