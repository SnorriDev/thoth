package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.CastEvent;
import snorri.parser.Node;

public class Move extends TransVerbDef {

	public Move() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, CastEvent e) {
		
		Object obj = object.getMeaning(e);
		
		if (e.getDestination() == null || e.getWorld().isOccupied(e.getDestination())) {
			return false;
		}
		
		if (obj instanceof Entity) {
			e.getWorld().getEntityTree().move((Entity) obj, e.getDestination());
			return true;
		}
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj, CastEvent e) {
		return false;
	}
	
	@Override
	public boolean altersMovement() {
		return true;
	}

	@Override
	public String toString() {
		return "teleport";
	}

}
