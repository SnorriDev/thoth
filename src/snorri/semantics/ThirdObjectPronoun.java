package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.Noun;

public class ThirdObjectPronoun extends Definition {

	public ThirdObjectPronoun() {
		super(Noun.class);
	}

	@Override
	public Object getMeaning(SpellEvent e) {
		return e.getThirdPerson();
	}

	@Override
	public String toString() {
		return "it (mouse)";
	}

}
