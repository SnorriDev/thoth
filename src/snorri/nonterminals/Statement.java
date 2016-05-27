package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.semantics.VerbDef;

public class Statement extends NonTerminal {

	public Object getMeaning(SpellEvent e) {
		
		return ((VerbDef) children.get(0)).eval(children.get(1).getMeaning(e), (children.size() == 2) ? null : children.get(2).getMeaning(e));
		
	}

}
