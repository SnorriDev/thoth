package snorri.semantics;

import snorri.events.SpellEvent;

public class With extends PrepDef {

	@Override
	public SpellEvent getModified(Nominal obj) {
		
		e.setInstrument(obj);
		return e;
		
	}

	@Override
	public String getShortDesc() {
		return "with";
	}

}
