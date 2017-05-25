package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.semantics.ConnectiveDef;
import snorri.semantics.VerbDef;

public class Statement extends NonTerminal {

	private static final long serialVersionUID = 1L;

	public Object getMeaning(SpellEvent e) {
		
		if (children.size() == 3 && children.get(1) instanceof Connective) {
			boolean arg1 = (boolean) children.get(0).getMeaning(e);
			boolean arg2 = (boolean) children.get(2).getMeaning(e);
			return ((ConnectiveDef) children.get(1).getMeaning(e)).eval(arg1, arg2);
		}
		
		boolean meaning = ((VerbDef) children.get(0).getMeaning(e)).eval(children.get(1).getMeaning(e),
				(children.size() == 2) ? null : children.get(2).getMeaning(e));
		boolean negated = e.isNegated();
		return (meaning && !negated) || (!meaning && negated);
		
	}

}
