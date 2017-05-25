package snorri.nonterminals;

import snorri.events.SpellEvent;
import snorri.semantics.Nominal;
import snorri.semantics.PrepDef;

public class PrepPhrase extends NonTerminal<SpellEvent> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @return a modified copy of SpellEvent which carries the necessary semantics
	 */
	public SpellEvent getMeaning(SpellEvent e) {
		
		//the supplied object is a class, not an object
		if (!(children.get(1).getMeaning(e) instanceof Nominal)) {
			return null;
		}
		
		PrepDef prep = (PrepDef) children.get(0).getMeaning(e);
		return ((PrepDef) prep.getMeaning(e)).getModified((Nominal) children.get(1).getMeaning(e));
		
	}

}