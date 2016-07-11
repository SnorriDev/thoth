package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.semantics.VerbDef;

public class Command extends NonTerminal {

	private static final long serialVersionUID = 1L;

	public Object getMeaning(SpellEvent e) {
		
		if (e.isNegated()) {
			return false;
		}
				
		switch (children.size()) {
		case 1:
			return ((VerbDef) children.get(0).getMeaning(e)).exec(null);
		case 2:
			return ((VerbDef) children.get(0).getMeaning(e)).exec(children.get(1).getMeaning(e));
		default:
			if ((boolean) children.get(2).getMeaning(e)) {
				return children.get(0).getMeaning(e);
			}
			return false; //imperative logical system, so statements are vacuously false
		}
		
	}

}
