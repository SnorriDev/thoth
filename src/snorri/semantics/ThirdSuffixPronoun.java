package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.SuffixPronoun;

public class ThirdSuffixPronoun extends Definition {

	public ThirdSuffixPronoun() {
		super(SuffixPronoun.class);
	}

	@Override
	public Object getMeaning(SpellEvent e) {
		return e.getThirdPerson();
	}

	@Override
	public String toString() {
		return "it";
	}
	
}
