package snorri.semantics;

import snorri.events.SpellEvent;

public class Not extends AdverbDef {

	@Override
	public SpellEvent getMeaning(SpellEvent e) {
		return e.getNegated();
	}

	@Override
	public String toString() {
		return "not";
	}

}
