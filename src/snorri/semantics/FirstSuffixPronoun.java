package snorri.semantics;

import snorri.events.CastEvent;
import snorri.events.CastEvent.Caster;
import snorri.nonterminals.SuffixPronoun;

public class FirstSuffixPronoun extends Definition<Caster> {

	public FirstSuffixPronoun() {
		super(SuffixPronoun.class, Caster.class);
	}

	@Override
	public Caster getMeaning(CastEvent e) {
		return e.getFirstPerson();
	}

	@Override
	public String toString() {
		return "I (player)";
	}

	
	
}
