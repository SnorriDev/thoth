package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.semantics.Nominal.AbstractSemantics;
import snorri.world.Vector;

public class To extends PrepDef {

	@Override
	public SpellEvent getModified(Nominal obj) {
		
		Object pos = obj.get(e.getWorld(), AbstractSemantics.POSITION);
		
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
