package snorri.nonterminals;

import snorri.events.CastEvent;
import snorri.parser.Node;
import snorri.semantics.Lambda;

public class Sentence extends NonTerminal<Boolean> {
	
	@Override @SuppressWarnings("unchecked")
	public Boolean getMeaning(CastEvent e) {
		
		if (children.size() == 1) {
			return (boolean) children.get(0).getMeaning(e);
		}
		
		if (children.size() == 3 && children.get(1) instanceof Connective) {
			Node<Boolean> arg1 = (Node<Boolean>) children.get(0);
			Node<Boolean> arg2 = (Node<Boolean>) children.get(2);
			Lambda<Boolean, Lambda<Boolean, Boolean>> lambda3 = (Lambda<Boolean, Lambda<Boolean, Boolean>>) children.get(1).getMeaning(e);
			return lambda3.exec(arg1).exec(arg2);
		}
		
		if (children.get(0) instanceof AdverbPhrase) {
			return (boolean) children.get(1).getMeaning((CastEvent) children.get(0).getMeaning(e));
		}
		
		return (boolean) children.get(0).getMeaning((CastEvent) children.get(1).getMeaning(e));
		
	}
	
	// believe this casting is fine, but untested
	@Override
	public Sentence copy() {
		return (Sentence) super.copy();
	}
	
	/**
	 * @return <code>true</code> if the sentence has side effect
	 */
	public boolean isStatement() {
		return children.get(0) instanceof Statement;
	}

}
