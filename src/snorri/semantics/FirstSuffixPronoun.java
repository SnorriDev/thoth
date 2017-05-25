package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.events.SpellEvent.Caster;
import snorri.nonterminals.SuffixPronoun;

public class FirstSuffixPronoun extends Definition<Caster> {

	public FirstSuffixPronoun() {
		super(SuffixPronoun.class, Caster.class);
	}

	@Override
	public Caster getMeaning(SpellEvent e) {
		return e.getFirstPerson();
	}

	@Override
	public String toString() {
		return "I (player)";
	}

	
	
}
