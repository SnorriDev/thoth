package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.parser.Terminal;

public class SemiTerminal<S> extends NonTerminal<S> {
	
	@SuppressWarnings("unchecked")
	public S getMeaning(SpellEvent e) {
		return ((Terminal<S>) children.get(0)).getMeaning(e);
	}

}
