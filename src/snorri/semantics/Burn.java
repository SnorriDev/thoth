package snorri.semantics;

import snorri.entities.Unit;
import snorri.modifiers.BurnModifier;

public class Burn extends VerbDef {

	//TODO: maybe this verb is actually transitive?
	
	public Burn() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		if (obj instanceof Unit) {
			((Unit) obj).addModifier(new BurnModifier());
		}
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return obj instanceof Unit && ((Unit) obj).hasModifier(BurnModifier.class);
	}

	@Override
	public String toString() {
		return "burn";
	}

}
