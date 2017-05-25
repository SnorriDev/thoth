package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.nonterminals.Noun;

public class SecondObjectPronoun extends Definition<Entity> {

	public SecondObjectPronoun() {
		super(Noun.class, Entity.class);
	}

	@Override
	public Entity getMeaning(SpellEvent e) {
		return e.getSecondPerson();
	}

	@Override
	public String toString() {
		return "you";
	}

}
