package snorri.semantics;

import snorri.entities.Entity;

public class Move extends VerbDef {

	public Move() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		if (obj instanceof Entity) {
			e.getWorld().getEntityTree().delete((Entity) obj);
			((Entity) obj).setPos(e.getDestination().copy());
			e.getWorld().getEntityTree().insert((Entity) obj);
			return true;
		}
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return false;
	}
	
	@Override
	public boolean altersMovement() {
		return true;
	}

}
