package snorri.semantics;

import snorri.events.SpellEvent;

public class Greatly extends AdverbDef {
	
	@Override
	public SpellEvent getMeaning(SpellEvent e) {
		return new SpellEvent(e).scaleHealthInteractionModifier(1.3 + 0.3 * e.pollDegree());
	}

	@Override
	public String toString() {
		return "greatly";
	}
	
}
