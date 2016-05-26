package snorri.parser;

import snorri.events.SpellEvent;

public abstract class Definition {

	private Class<? extends NonTerminal> partOfSpeech;
	
	public Definition(Class<? extends NonTerminal> partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}
	
	public Class<? extends NonTerminal> getPOS() {
		return partOfSpeech;
	}
	
	public abstract Object getMeaning(SpellEvent e);
	
}
