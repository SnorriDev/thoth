package snorri.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import snorri.inventory.DropContainer;
import snorri.inventory.Droppable;
import snorri.inventory.VocabDrop;

public class Lexicon extends HashSet<String> implements DropContainer<Droppable>, Serializable {
	
	/**
	 * A lexicon is a set of strings that an entity knows. These strings have meanings defined in the static default lexicon.
	 */
	private static final long serialVersionUID = 1L;
		
	public static Collection<Droppable> getDropsInLang() {
		List<Droppable> out = new ArrayList<>();
		for (String raw : DefaultLexicon.getOrthographicForms()) {
			out.add(new VocabDrop(raw));
		}
		return out;
	}

	@Override
	public boolean add(Droppable d) {
		if (d instanceof VocabDrop) {
			return add(((VocabDrop) d).getOrthography());
		}
		return false;
	}
	
	@Override
	public boolean remove(Droppable d, boolean specific) {
		if (d instanceof VocabDrop) {
			return remove(((VocabDrop) d).getOrthography(), specific);
		}
		return false;
	}
	
	public boolean remove(String word, boolean specific) {
		return super.remove(word);
	}
		
	public boolean contains(Collection<String> words) {
		for (String word : words) {
			if (!contains(word)) {
				return false;
			}
		}
		return true;
	}
	
}
