package snorri.semantics;

import snorri.entities.Entity;

public class Burn extends VerbDef {

	//TODO: maybe this verb is actually transitive?
	
	public Burn() {
		super(false);
	}

	@Override
	public boolean exec(Object obj) {
		e.getSecondPerson().burn();
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return subj instanceof Entity && ((Entity) subj).isBurning();
	}

}
