package snorri.nonterminals;

import snorri.events.CastEvent;
import snorri.parser.Node;

public class AdverbPhrase extends NonTerminal<CastEvent> {

	@Override @SuppressWarnings("unchecked")
	public CastEvent getMeaning(CastEvent e) {
				
		if (children.size() == 2) {
			//restricting access of AdverbModifiers to degree
			CastEvent copy = new CastEvent(e, (int) children.get(0).getMeaning(e));
			return ((Node<CastEvent>) children.get(1)).getMeaning(copy);
		}
		
		return ((Node<CastEvent>) children.get(0)).getMeaning(e);
		
	}

}
