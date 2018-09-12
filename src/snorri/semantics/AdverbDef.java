package snorri.semantics;

import snorri.events.CastEvent;
import snorri.nonterminals.Adverb;

public abstract class AdverbDef extends Definition<CastEvent> {

	/**
	 * the getMeaning method for an adverb should return a modified SpellEvent
	 */
	
	public AdverbDef() {
		super(Adverb.class, CastEvent.class);
	}

}
