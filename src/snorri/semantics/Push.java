package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.CastEvent;
import snorri.main.Debug;
import snorri.parser.Node;
import snorri.world.Vector;

public class Push extends TransVerbDef {
	
	private static Vector DELTA = new Vector(0, -10);
	private static double FORCE = 600d;

	@Override
	public boolean eval(Object subject, Object object, CastEvent e) {
		return false;
	}

	@Override
	public boolean exec(Node<Object> object, CastEvent e) {
		Entity entity = (Entity) object.getMeaning(e);
		if (entity.isStaticObject()) {
			Debug.logger.info("Can't push static object.");
			return false;
		}
		
		Vector target = e.getDestination();
		Vector velocity = target.sub(entity.getPos()).scale(FORCE);
		entity.setPos(entity.getPos().add(DELTA));
		entity.setVelocity(velocity);
		return true;
	}

	@Override
	public String toString() {
		return "push";
	}

}
