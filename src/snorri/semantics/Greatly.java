package snorri.semantics;

import snorri.events.SpellEvent;

public class Greatly extends AdverbDef {
	
	@Override
	public Object getMeaning(SpellEvent e) {
		return new SpellEvent(e).scaleHealthInteractionModifier(1.3 + 0.3 * e.pollDegree());
	}
	
}
