package snorri.nonterminals;

import snorri.events.SpellEvent;

public class SemiTerminal extends NonTerminal {

	private static final long serialVersionUID = 1L;
	
	public Object getMeaning(SpellEvent e) {
		return children.get(0).getMeaning(e);
	}

}
