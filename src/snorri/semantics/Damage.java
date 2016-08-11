package snorri.semantics;

import snorri.entities.Unit;

public class Damage extends VerbDef {

	private static final int AMOUNT = 30;
	
	public Damage() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		if (obj instanceof Unit) {
			((Unit) obj).damage(AMOUNT, e);
		}
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		//TODO come up with something here?
		return false;
	}

	@Override
	public String toString() {
		return "hurt";
	}

}
