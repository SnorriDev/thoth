package snorri.nonterminals;

import snorri.events.CastEvent;
import snorri.parser.Node;
import snorri.semantics.Lambda;

public class Command extends NonTerminal<Boolean> {

	@Override @SuppressWarnings("unchecked")
	public Boolean getMeaning(CastEvent e) {
		
		if (e.isNegated()) {
			return false;
		}
		
		//TODO make this all more elegant
		
		switch (children.size()) {
		case 1:
			Lambda<Object, Boolean> lambda = (Lambda<Object, Boolean>) children.get(0).getMeaning(e);
			return lambda.exec(null);
		case 2:
			Lambda<Object, Lambda<Object, Boolean>> lambda2 = (Lambda<Object, Lambda<Object, Boolean>>) children.get(0).getMeaning(e);
			return lambda2.exec(null).exec((Node<Object>) children.get(1));
		case 3:
			Node<Boolean> arg1 = (Node<Boolean>) children.get(0);
			Node<Boolean> arg2 = (Node<Boolean>) children.get(2);
			Lambda<Boolean, Lambda<Boolean, Boolean>> lambda3 = (Lambda<Boolean, Lambda<Boolean, Boolean>>) children.get(1).getMeaning(e);
			return lambda3.exec(arg1).exec(arg2);
		}
		
		return null;
		
	}

}
