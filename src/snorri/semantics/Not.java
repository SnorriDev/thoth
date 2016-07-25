package snorri.semantics;

import snorri.events.SpellEvent;

public class Not extends AdverbDef {

	@Override
	public Object getMeaning(SpellEvent e) {
		return e.getNegated();
	}

	@Override
	public String getShortDesc() {
		return "not";
	}

}
