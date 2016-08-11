package snorri.semantics;

import snorri.entities.Entity;

public class Be extends VerbDef {

	public Be() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		
		// TODO add more tests for equivalence
		if (subj instanceof Entity && obj instanceof Entity) {
			return subj == obj;
		}
		
		return false;
	}

	@Override
	public String toString() {
		return "be";
	}

}
