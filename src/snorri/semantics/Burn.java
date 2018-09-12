package snorri.semantics;

import snorri.entities.Unit;
import snorri.events.CastEvent;
import snorri.modifiers.BurnModifier;
import snorri.parser.Node;

public class Burn extends TransVerbDef {
	
	public Burn() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, CastEvent e) {
		Object obj = object.getMeaning(e);
		if (obj instanceof Unit) {
			((Unit) obj).addModifier(new BurnModifier());
		}
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj, CastEvent e) {
		return obj instanceof Unit && ((Unit) obj).hasModifier(BurnModifier.class);
	}

	@Override
	public String toString() {
		return "burn";
	}

}
