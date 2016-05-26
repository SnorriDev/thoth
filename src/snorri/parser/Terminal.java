package snorri.parser;

import snorri.events.SpellEvent;

public class Terminal implements Node {

	private String orthography;
	
	public Terminal(String orthography) {
		this.orthography = orthography;
	}
	
	public boolean equals(Terminal other) {
		return orthography.equals(other.orthography);
	}
	
	public boolean equals(String other) {
		return orthography.equals(other);
	}
	
	public Object getOrthography() {
		return orthography;
	}
	
	public Object getMeaning(SpellEvent e) {
		if (Lexicon.lookup(orthography) != null)
			return Lexicon.lookup(orthography).getMeaning(e);
		return null;
	}
	
	public String toString() {
		return "[" + orthography + "]";
	}
	
}
