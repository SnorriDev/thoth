package snorri.semantics;

import snorri.events.CastEvent;
import snorri.semantics.Nominal.AbstractSemantics;
import snorri.world.Vector;

public class Above extends PrepDef {

	@Override
	public CastEvent eval(Nominal obj, CastEvent e) {
		
		Object pos = obj.get(AbstractSemantics.POSITION, e);
		
		if (pos instanceof Vector) {
			Vector v = ((Vector) pos).copy().sub_(new Vector(0, DISPLACE_DISTANCE));
			e.setLocative(v); //e is a copy of the SpellEvent, not the real thing
			e.setDestination(v);
		}
		
		return e;
		
	}

	@Override
	public String toString() {
		return "above";
	}

}
