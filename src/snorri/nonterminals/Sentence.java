package snorri.nonterminals;

import snorri.events.SpellEvent;

public class Sentence extends NonTerminal {

	private static final long serialVersionUID = 1L;
	
	public Object getMeaning(SpellEvent e) {
		
		if (children.size() == 1) {
			return children.get(0).getMeaning(e);
		}
		
		if (children.get(0) instanceof AdverbPhrase) {
			return children.get(1).getMeaning((SpellEvent) children.get(0).getMeaning(e));
		}
		
		return children.get(0).getMeaning((SpellEvent) children.get(1).getMeaning(e));
		
	}
	
	/**
	 * @return <code>true</code> if the sentence has side effect
	 */
	public boolean isStatement() {
		return children.get(0) instanceof Statement;
	}

}
