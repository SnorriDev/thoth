package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.events.SpellEvent.Caster;
import snorri.nonterminals.Noun;

public class FirstObjectPronoun extends Definition<Caster> {

	public FirstObjectPronoun() {
		super(Noun.class, Caster.class);
	}

	@Override
	public Caster getMeaning(SpellEvent e) {
		return e.getFirstPerson();
	}

	@Override
	public String toString() {
		return "me (player)";
	}

}
