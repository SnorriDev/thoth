package snorri.inventory;

import snorri.parser.Lexicon;
import snorri.semantics.Definition;

public class VocabDrop implements Droppable {

	private static final long serialVersionUID = 1L;
	private String orthography;
	
	public VocabDrop(String orthography) {
		this.orthography = orthography;
	}
	
	public String getOrthography() {
		return orthography;
	}
	
	public Definition getMeaning() {
		return Lexicon.lookup(orthography);
	}
	
	@Override
	public boolean stack(Droppable other) {
		return false;
	}
	
	@Override
	public int getMaxQuantity() {
		return 1;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof VocabDrop)) {
			return false;
		}
		return orthography.equals(((VocabDrop) other).orthography);
	}
	
	@Override
	public String toString() {
		return orthography;
	}
	
}