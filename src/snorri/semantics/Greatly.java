package snorri.semantics;

import snorri.events.CastEvent;

public class Greatly extends AdverbDef {
	
	@Override
	public CastEvent getMeaning(CastEvent e) {
		return new CastEvent(e).scaleHealthInteractionModifier(1.3 + 0.3 * e.pollDegree());
	}

	@Override
	public String toString() {
		return "greatly";
	}
	
}
