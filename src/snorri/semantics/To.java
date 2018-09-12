package snorri.semantics;

import snorri.events.CastEvent;
import snorri.semantics.Nominal.AbstractSemantics;
import snorri.world.Vector;

public class To extends PrepDef {

	@Override
	public CastEvent eval(Nominal obj, CastEvent e) {
		
		Object pos = obj.get(AbstractSemantics.POSITION, e);
		
		if (pos instanceof Vector) {
			e.setDestination(((Vector) pos).copy()); //e is a copy of the SpellEvent, not the real thing
		}
		
		return e;
		
	}

	@Override
	public String toString() {
		return "to";
	}

}
