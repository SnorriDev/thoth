package snorri.semantics;

import snorri.entities.Entity;

public class Fly extends VerbDef {

	public Fly() {
		super(false);
	}

	@Override
	public boolean exec(Object obj) {
		e.getSecondPerson().startFlying();
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		if (subj instanceof Entity) {
			return ((Entity) subj).isFlying();
		}
		return false;
	}

}
