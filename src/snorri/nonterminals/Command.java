package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.semantics.ConnectiveDef;
import snorri.semantics.VerbDef;

public class Command extends NonTerminal {

	private static final long serialVersionUID = 1L;

	public Object getMeaning(SpellEvent e) {
		
		if (e.isNegated()) {
			return false;
		}
		
		//TODO maybe clean up this gross system of semantics
		
		switch (children.size()) {
		case 1:
			return ((VerbDef) children.get(0).getMeaning(e)).exec(null);
		case 2:
			return ((VerbDef) children.get(0).getMeaning(e)).exec(children.get(1).getMeaning(e));
		default:
			return ((ConnectiveDef) children.get(1).getMeaning(e)).exec(children.get(0), children.get(2));
		}
		
	}

}
