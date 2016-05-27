package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.NonTerminal;
import snorri.nonterminals.NounPhrase;

public abstract class VerbDef extends Definition {
	
	public enum Mood {
		INDICATIVE, IMPERATIVE;
	}
	
	public VerbDef(Class<? extends NonTerminal> partOfSpeech) {
		super(partOfSpeech);
	}

	@Override
	public Object getMeaning(SpellEvent e) {
		try {
			return getClass().getMethod("wrap", NounPhrase.class, NounPhrase.class, Mood.class);
		} catch (Exception e1) {
			return null; //never reachable
		}
	}
	
	@SuppressWarnings("unused")
	private Object wrap(NounPhrase subj, NounPhrase obj, Mood mood) {
		if (mood.equals(Mood.INDICATIVE)) {
			return eval(subj, obj);
		}
		exec(obj);
		return null;
	}
	
	//TODO: add modifiers
	
	/**
	 * do the action associated with the imperative mood of this verb
	 * @param obj the direct object associated with a verb (null for intransitive usage)
	 */
	protected abstract void exec(NounPhrase obj);

	/**
	 * evaluate the state associated with the indicative mood of this verb
	 * @param subj the subject
	 * @param obj the direct object (null for intransitive usage)
	 * @return the truth value of the statement in the game world
	 */
	protected abstract boolean eval(NounPhrase subject, NounPhrase obj);

}
