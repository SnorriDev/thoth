package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.semantics.Nominal;
import snorri.semantics.Nominal.AbstractSemantics;

public class NounPhrase extends NonTerminal<Object> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getMeaning(SpellEvent e) {
		
		if (children.size() == 1) {
			return children.get(0).getMeaning(e);
		}
		
		return ((Nominal) children.get(1).getMeaning(e)).get(e.getWorld(), (AbstractSemantics) children.get(0).getMeaning(e));
		
	}

}
