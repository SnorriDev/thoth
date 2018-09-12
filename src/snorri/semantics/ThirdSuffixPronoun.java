package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.CastEvent;
import snorri.nonterminals.SuffixPronoun;

public class ThirdSuffixPronoun extends Definition<Entity> {

	//TODO create type hierarchy for pronouns
	
	public ThirdSuffixPronoun() {
		super(SuffixPronoun.class, Entity.class);
	}

	@Override
	public Entity getMeaning(CastEvent e) {
		return e.getThirdPerson();
	}

	@Override
	public String toString() {
		return "it (mouse)";
	}
	
}
