package snorri.semantics;

import snorri.entities.Unit;
import snorri.events.SpellEvent;
import snorri.modifiers.SlowModifier;
import snorri.parser.Node;

public class Slow extends TransVerbDef {

	public Slow() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		Object obj = object.getMeaning(e);
		if (obj instanceof Unit) {
			((Unit) obj).addModifier(new SlowModifier());
		}
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		return obj instanceof Unit && ((Unit) obj).hasModifier(SlowModifier.class);
	}

	@Override
	public String toString() {
		return "slow";
	}

}
