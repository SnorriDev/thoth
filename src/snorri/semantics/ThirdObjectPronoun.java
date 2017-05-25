package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.nonterminals.Noun;

public class ThirdObjectPronoun extends Definition<Entity> {

	public ThirdObjectPronoun() {
		super(Noun.class, Entity.class);
	}

	@Override
	public Entity getMeaning(SpellEvent e) {
		return e.getThirdPerson();
	}

	@Override
	public String toString() {
		return "it (mouse)";
	}

}
