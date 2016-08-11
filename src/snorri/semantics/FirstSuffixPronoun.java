package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.SuffixPronoun;

public class FirstSuffixPronoun extends Definition {

	public FirstSuffixPronoun() {
		super(SuffixPronoun.class);
	}

	@Override
	public Object getMeaning(SpellEvent e) {
		return e.getFirstPerson();
	}

	@Override
	public String toString() {
		return "I";
	}

	
	
}
