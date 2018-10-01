package snorri.semantics;

import snorri.events.CastEvent;

public class With extends PrepDef {

	@Override
	public CastEvent eval(Nominal obj, CastEvent e) {
		
		e.setInstrument(obj);
		return e;
		
	}

	@Override
	public String toString() {
		return "with";
	}

}
