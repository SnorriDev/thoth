package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.Prep;

public abstract class PrepDef extends Definition {
	
	protected SpellEvent e;
	
	public PrepDef() {
		super(Prep.class);
	}

	@Override
	public Object getMeaning(SpellEvent e) {
		this.e = new SpellEvent(e);
		return this;
	}
		
	/**
	 * do the action associated with the imperative mood of this verb
	 * @param obj the direct object associated with a verb (null for intransitive usage)
	 * @return a location
	 */
	public abstract SpellEvent getModified(Object obj);

}