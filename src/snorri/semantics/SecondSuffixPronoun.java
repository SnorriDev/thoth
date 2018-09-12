package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.CastEvent;
import snorri.nonterminals.SuffixPronoun;

public class SecondSuffixPronoun extends Definition<Entity> {

	public SecondSuffixPronoun() {
		super(SuffixPronoun.class, Entity.class);
	}

	@Override
	public Entity getMeaning(CastEvent e) {
		return e.getSecondPerson();
	}

	@Override
	public String toString() {
		return "you";
	}
	
}
