package snorri.semantics;

import snorri.entities.Entity;

public class Burn extends VerbDef {

	//TODO: maybe this verb is actually transitive?
	
	public Burn() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		if (obj instanceof Entity) {
			((Entity) obj).burn();
		}
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return obj instanceof Entity && ((Entity) obj).isBurning();
	}

	@Override
	public String toString() {
		return "burn";
	}

}
