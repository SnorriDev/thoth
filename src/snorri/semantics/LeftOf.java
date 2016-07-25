package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.semantics.Nominal.AbstractSemantics;
import snorri.world.Vector;

public class LeftOf extends PrepDef {

	@Override
	public SpellEvent getModified(Nominal obj) {
		
		Object pos = obj.get(e.getWorld(), AbstractSemantics.POSITION);
		
		if (pos instanceof Vector) {
			Vector v = ((Vector) pos).copy().sub(new Vector(DISPLACE_DISTANCE, 0));
			e.setLocative(v); //e is a copy of the SpellEvent, not the real thing
			e.setDestination(v);
		}
		
		return e;
		
	}

	@Override
	public String getShortDesc() {
		return "left of";
	}

}
