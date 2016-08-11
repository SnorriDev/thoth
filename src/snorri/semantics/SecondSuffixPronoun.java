package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.SuffixPronoun;

public class SecondSuffixPronoun extends Definition {

	public SecondSuffixPronoun() {
		super(SuffixPronoun.class);
	}

	@Override
	public Object getMeaning(SpellEvent e) {
		return e.getSecondPerson();
	}

	@Override
	public String toString() {
		return "you";
	}
	
}
