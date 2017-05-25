package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.semantics.Lambda;

public class Statement extends NonTerminal<Boolean> {

	private static final long serialVersionUID = 1L;

	@Override @SuppressWarnings("unchecked")
	public Boolean getMeaning(SpellEvent e) {
		
		boolean meaning = false;
		switch (children.size()) {
		
		case 2:
			Lambda<Object, Boolean> lambda = (Lambda<Object, Boolean>) children.get(0);
			meaning = lambda.eval(children.get(1).getMeaning(e));
			break;
		case 3:
			
			//TODO there is an issue with statements
			
			if (children.get(0) instanceof TransVerb) {
				Lambda<Object, Lambda<Object, Boolean>> lambda2 = (Lambda<Object, Lambda<Object, Boolean>>) children.get(0).getMeaning(e);
				Object arg1 = children.get(1).getMeaning(e);
				Object arg2 = children.get(2).getMeaning(e);
				meaning = lambda2.eval(arg1).eval(arg2);
				break;
			}
			
			if (children.get(1) instanceof Connective) {
				boolean arg1 = (boolean) children.get(0).getMeaning(e);
				boolean arg2 = (boolean) children.get(2).getMeaning(e);
				Lambda<Boolean, Lambda<Boolean, Boolean>> lambda3 = (Lambda<Boolean, Lambda<Boolean, Boolean>>) children.get(1).getMeaning(e);
				return lambda3.eval(arg1).eval(arg2);
			}
			
		}
		
		boolean negated = e.isNegated();
		return (meaning && !negated) || (!meaning && negated);
		
	}

}
