package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.semantics.Nominal;
import snorri.semantics.Nominal.AbstractSemantics;

public class NounPhrase extends NonTerminal<Nominal> {

	private static final long serialVersionUID = 1L;

	@Override
	public Nominal getMeaning(SpellEvent e) {
		
		if (children.size() == 1) {
			return (Nominal) children.get(0).getMeaning(e);
		}
		
		return (Nominal) ((Nominal) children.get(1).getMeaning(e)).get(e.getWorld(), (AbstractSemantics) children.get(0).getMeaning(e));
		
	}

}
