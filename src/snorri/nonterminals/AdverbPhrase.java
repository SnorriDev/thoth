package snorri.nonterminals;

import snorri.events.SpellEvent;

public class AdverbPhrase extends NonTerminal {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getMeaning(SpellEvent e) {
				
		if (children.size() == 2) {
			//restricting access of AdverbModifiers to degree
			SpellEvent copy = new SpellEvent(e, (int) children.get(0).getMeaning(e));
			return children.get(1).getMeaning(copy);
		}
		
		return children.get(0).getMeaning(e);
		
	}

}
