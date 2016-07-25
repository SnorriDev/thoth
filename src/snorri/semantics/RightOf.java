package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.semantics.Nominal.AbstractSemantics;
import snorri.world.Vector;

public class RightOf extends PrepDef {

	@Override
	public SpellEvent getModified(Nominal obj) {
		
		Object pos = obj.get(e.getWorld(), AbstractSemantics.POSITION);
		
		if (pos instanceof Vector) {
			Vector v = ((Vector) pos).copy().add(new Vector(DISPLACE_DISTANCE, 0));
			e.setLocative(v); //e is a copy of the SpellEvent, not the real thing
			e.setDestination(v);
		}
		
		return e;
		
	}

	@Override
	public String getShortDesc() {
		return "right of";
	}

}
