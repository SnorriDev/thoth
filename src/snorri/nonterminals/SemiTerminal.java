package snorri.nonterminals;

import snorri.events.CastEvent;
import snorri.parser.Terminal;

public class SemiTerminal<S> extends NonTerminal<S> {
	
	@SuppressWarnings("unchecked")
	public S getMeaning(CastEvent e) {
		return ((Terminal<S>) children.get(0)).getMeaning(e);
	}

}
