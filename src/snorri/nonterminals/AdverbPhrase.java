package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.parser.Node;

public class AdverbPhrase extends NonTerminal<SpellEvent> {

	private static final long serialVersionUID = 1L;

	@Override @SuppressWarnings("unchecked")
	public SpellEvent getMeaning(SpellEvent e) {
				
		if (children.size() == 2) {
			//restricting access of AdverbModifiers to degree
			SpellEvent copy = new SpellEvent(e, (int) children.get(0).getMeaning(e));
			return ((Node<SpellEvent>) children.get(1)).getMeaning(copy);
		}
		
		return ((Node<SpellEvent>) children.get(0)).getMeaning(e);
		
	}

}
