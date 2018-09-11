package snorri.semantics;

import snorri.events.CastEvent;

public class Not extends AdverbDef {

	@Override
	public CastEvent getMeaning(CastEvent e) {
		return e.getNegated();
	}

	@Override
	public String toString() {
		return "not";
	}

}
