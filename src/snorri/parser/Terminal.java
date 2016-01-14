package snorri.parser;

import snorri.semantics.Lexicon;

public class Terminal implements Node {

	private Object meaning;
	
	public Terminal(String orthography) {
		if (Lexicon.lookup(orthography) != null)
			this.meaning = Lexicon.lookup(orthography).getMeaning();
		else
			this.meaning = null;
	}
	
	public boolean equals(Terminal other) {
		return meaning == other.meaning || meaning.equals(other.meaning);
	}
	
	public Object getMeaning() {
		return meaning;
	}
	
}
