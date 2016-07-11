package snorri.semantics;

import snorri.events.SpellEvent;

public class Greatly extends AdverbDef {
	
	@Override
	public Object getMeaning(SpellEvent e) {
		return new SpellEvent(e).scaleSizeModifier(1.3 + 0.3 * e.pollDegree());
	}
	
}
