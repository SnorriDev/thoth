package snorri.semantics;

import snorri.events.CastEvent;
import snorri.events.CastEvent.Caster;
import snorri.nonterminals.Noun;

public class FirstObjectPronoun extends Definition<Caster> {

	public FirstObjectPronoun() {
		super(Noun.class, Caster.class);
	}

	@Override
	public Caster getMeaning(CastEvent e) {
		return e.getFirstPerson();
	}

	@Override
	public String toString() {
		return "me (player)";
	}

}
