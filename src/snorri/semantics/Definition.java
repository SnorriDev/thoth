package snorri.semantics;

import snorri.events.CastEvent;
import snorri.nonterminals.SemiTerminal;

public abstract class Definition<S> {

	//maybe we can pass a type parameter here
	
	private final Class<? extends SemiTerminal<?>> partOfSpeech;
	/**
	 * Semantic category of a value is Class<S>, of a function is Category(A, B)
	 */
	private final Object semanticCategory;
	
	protected Definition(Class<? extends SemiTerminal<?>> partOfSpeech, Object semanticCategory) {
		this.partOfSpeech = partOfSpeech;
		this.semanticCategory = semanticCategory;
	}
	
	public Class<? extends SemiTerminal<?>> getPOS() {
		return partOfSpeech;
	}
	
	public Object getCategory() {
		return semanticCategory;
	}
	
	public boolean isPOS(Class<? extends SemiTerminal<?>> c) {
		return c.equals(partOfSpeech);
	}
	
	public abstract S getMeaning(CastEvent e);

	/**
	 * @return a short one or two-word description of this word
	 */
	public abstract String toString();
	
	/**
	 * @return a longer description of what this word does
	 */
	public String getLongDesc() {
		return null;
	}
	
	public boolean altersMovement() {
		return false;
	}
	
}
