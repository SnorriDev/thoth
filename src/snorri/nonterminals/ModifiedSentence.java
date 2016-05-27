package snorri.nonterminals;

import snorri.events.SpellEvent;

public class ModifiedSentence extends NonTerminal {

	public Object getMeaning(SpellEvent e) {
		
		if (children.get(0) instanceof PrepPhrase) {
			return children.get(1).getMeaning((SpellEvent) children.get(0).getMeaning(e));
		}
		
		return children.get(0).getMeaning((SpellEvent) children.get(1).getMeaning(e));
		
	}

}
