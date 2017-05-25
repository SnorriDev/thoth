package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.parser.Node;

public class Be extends TransVerbDef {

	public Be() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		
		// TODO add more tests for equivalence
		if (subj instanceof Entity && obj instanceof Entity) {
			return subj == obj;
		}
		
		return false;
	}

	@Override
	public String toString() {
		return "be";
	}

}
