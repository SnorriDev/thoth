package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.Adverb;

public abstract class AdverbDef extends Definition<SpellEvent> {

	/**
	 * the getMeaning method for an adverb should return a modified SpellEvent
	 */
	
	public AdverbDef() {
		super(Adverb.class, SpellEvent.class);
	}

}
