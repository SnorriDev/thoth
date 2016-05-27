package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.Noun;

public class SecondObjectPronoun extends Definition {

	public SecondObjectPronoun() {
		super(Noun.class);
	}

	@Override
	public Object getMeaning(SpellEvent e) {
		return e.getSecondPerson();
	}

}
