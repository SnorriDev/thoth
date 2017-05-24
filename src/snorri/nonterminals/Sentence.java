package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.main.Main;
import snorri.semantics.ConnectiveDef;

public class Sentence extends NonTerminal {

	private static final long serialVersionUID = 1L;
	
	public Object getMeaning(SpellEvent e) {
		
		if (children.size() == 1) {
			return children.get(0).getMeaning(e);
		}
		
		Main.debug(children.size());
		Main.debug(children.get(1));
		if (children.size() == 3 && children.get(1).isConnective()) {
			return ((ConnectiveDef) children.get(1).getMeaning(e)).exec(children.get(0), children.get(2));
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
