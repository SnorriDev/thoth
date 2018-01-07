package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.semantics.Nominal.AbstractSemantics;
import snorri.world.Vector;

public class RightOf extends PrepDef {

	@Override
	public SpellEvent eval(Nominal obj, SpellEvent e) {
		
		Object pos = obj.get(AbstractSemantics.POSITION, e);
		
		if (pos instanceof Vector) {
			Vector v = ((Vector) pos).copy().add_(new Vector(DISPLACE_DISTANCE, 0));
			e.setLocative(v); //e is a copy of the SpellEvent, not the real thing
			e.setDestination(v);
		}
		
		return e;
		
	}

	@Override
	public String toString() {
		return "right of";
	}

}
