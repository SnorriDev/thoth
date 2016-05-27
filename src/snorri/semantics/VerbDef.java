package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.IntransVerb;
import snorri.nonterminals.TransVerb;

public abstract class VerbDef extends Definition {
	
	protected SpellEvent e;
	
	public VerbDef(boolean trans) {
		super(trans ? TransVerb.class : IntransVerb.class);
	}

	@Override
	public Object getMeaning(SpellEvent e) {
		this.e = e;
		return this;
	}
		
	/**
	 * do the action associated with the imperative mood of this verb
	 * @param obj the direct object associated with a verb (null for intransitive usage)
	 * @return whether or not any action has been taken (imperative logic)
	 */
	public abstract boolean exec(Object obj);

	/**
	 * evaluate the state associated with the indicative mood of this verb
	 * @param subj the subject
	 * @param obj the direct object (null for intransitive usage)
	 * @return the truth value of the statement in the game world (boolean algebraic logic)
	 */
	public abstract boolean eval(Object subj, Object obj);

}
