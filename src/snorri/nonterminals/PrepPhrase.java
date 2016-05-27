package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.semantics.PrepDef;

public class PrepPhrase extends NonTerminal {

	/**
	 * @return a modified copy of SpellEvent which carries the necessary semantics
	 */
	public Object getMeaning(SpellEvent e) {
		return ((PrepDef) children.get(0).getMeaning(e)).getModified(children.get(1).getMeaning(e));
	}

}