package snorri.semantics;

import snorri.entities.Unit;
import snorri.events.CastEvent;
import snorri.modifiers.SlowModifier;
import snorri.parser.Node;

public class Slow extends TransVerbDef {

	public Slow() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, CastEvent e) {
		Object obj = object.getMeaning(e);
		if (obj instanceof Unit) {
			((Unit) obj).addModifier(new SlowModifier());
		}
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj, CastEvent e) {
		return obj instanceof Unit && ((Unit) obj).hasModifier(SlowModifier.class);
	}

	@Override
	public String toString() {
		return "slow";
	}

}
