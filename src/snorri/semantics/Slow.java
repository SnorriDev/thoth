package snorri.semantics;

import snorri.entities.Unit;
import snorri.modifiers.SlowModifier;

public class Slow extends VerbDef {

	public Slow() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		if (obj instanceof Unit) {
			((Unit) obj).addModifier(new SlowModifier());
		}
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return obj instanceof Unit && ((Unit) obj).hasModifier(SlowModifier.class);
	}

	@Override
	public String toString() {
		return "slow";
	}

}
