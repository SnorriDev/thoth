package snorri.semantics;

import snorri.events.SpellEvent;

public class With extends PrepDef {

	@Override
	public SpellEvent eval(Nominal obj, SpellEvent e) {
		
		e.setInstrument(obj);
		return e;
		
	}

	@Override
	public String toString() {
		return "with";
	}

}
