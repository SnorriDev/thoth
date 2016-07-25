package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.Noun;

public class FirstObjectPronoun extends Definition {

	public FirstObjectPronoun() {
		super(Noun.class);
	}

	@Override
	public Object getMeaning(SpellEvent e) {
		return e.getFirstPerson();
	}

	@Override
	public String getShortDesc() {
		return "me";
	}

}
