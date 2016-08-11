package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.SemiTerminal;

public abstract class Definition {

	//maybe we can pass a type parameter here
	
	private Class<? extends SemiTerminal> partOfSpeech;
	
	public Definition(Class<? extends SemiTerminal> partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}
	
	public Class<? extends SemiTerminal> getPOS() {
		return partOfSpeech;
	}
	
	public boolean isPOS(Class<? extends SemiTerminal> c) {
		return c.equals(partOfSpeech);
	}
	
	public abstract Object getMeaning(SpellEvent e);

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
