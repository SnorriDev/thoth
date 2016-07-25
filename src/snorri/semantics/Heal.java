package snorri.semantics;

import snorri.entities.Unit;

public class Heal extends VerbDef {

	private static final double AMOUNT = 10;
	
	public Heal() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		if (obj instanceof Unit) {
			((Unit) obj).heal(AMOUNT, e);
			return true;
		}
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return false;
	}

	@Override
	public String getShortDesc() {
		return "heal";
	}

}
