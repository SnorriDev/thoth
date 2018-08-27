package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.parser.Node;

public class Move extends TransVerbDef {

	public Move() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		
		Object obj = object.getMeaning(e);
		
		if (e.getDestination() != null && !e.getWorld().isPathable(e.getDestination())) {
			return false;
		}
		
		if (obj instanceof Entity) {
			e.getWorld().getEntityTree().move((Entity) obj, e.getDestination());
			return true;
		}
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
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
