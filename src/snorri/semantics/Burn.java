package snorri.semantics;

import snorri.entities.Unit;
import snorri.events.SpellEvent;
import snorri.modifiers.BurnModifier;
import snorri.parser.Node;

public class Burn extends TransVerbDef {
	
	public Burn() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		Object obj = object.getMeaning(e);
		if (obj instanceof Unit) {
			((Unit) obj).addModifier(new BurnModifier());
		}
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		return obj instanceof Unit && ((Unit) obj).hasModifier(BurnModifier.class);
	}

	@Override
	public String toString() {
		return "burn";
	}

}
