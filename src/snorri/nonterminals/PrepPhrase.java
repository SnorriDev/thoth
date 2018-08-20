package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.semantics.Lambda;
import snorri.semantics.Nominal;

public class PrepPhrase extends NonTerminal<SpellEvent> {
	
	/**
	 * @return a modified copy of SpellEvent which carries the necessary semantics
	 */
	@Override @SuppressWarnings("unchecked")
	public SpellEvent getMeaning(SpellEvent e) {
		
		//the supplied object is a class, not an object
		if (!(children.get(1).getMeaning(e) instanceof Nominal)) {
			return null;
		}
		
		Lambda<Nominal, SpellEvent> lambda = (Lambda<Nominal, SpellEvent>) children.get(0).getMeaning(e);
		return lambda.eval((Nominal) children.get(1).getMeaning(e));
		
	}

}