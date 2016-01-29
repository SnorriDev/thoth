package snorri.semantics;

import snorri.parser.NonTerminal;

public class Definition {

	private Class<? extends NonTerminal> partOfSpeech;
	private Object meaning;
	
	public Definition(Class<? extends NonTerminal> partOfSpeech, Object meaning) {
		this.partOfSpeech = partOfSpeech;
		this.meaning = meaning;
	}
	
	public Class<? extends NonTerminal> getPOS() {
		return partOfSpeech;
	}
	
	public Object getMeaning() {
		return meaning;
	}
	
}
