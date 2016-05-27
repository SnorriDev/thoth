package snorri.nonterminals;

import snorri.events.SpellEvent;

public class LowNonTerminal extends NonTerminal {

	public Object getMeaning(SpellEvent e) {
		return children.get(0).getMeaning(e);
	}

}
