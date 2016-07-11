package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.semantics.VerbDef;

public class Statement extends NonTerminal {

	private static final long serialVersionUID = 1L;
	
	public Object getMeaning(SpellEvent e) {
		boolean meaning = ((VerbDef) children.get(0).getMeaning(e)).eval(children.get(1).getMeaning(e), (children.size() == 2) ? null : children.get(2).getMeaning(e));
		boolean negated = e.isNegated();
		return (meaning && !negated) || (!meaning && negated);
	}

}
