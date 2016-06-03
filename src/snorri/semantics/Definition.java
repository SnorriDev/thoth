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

	public boolean altersMovement() {
		return false;
	}
	
}
