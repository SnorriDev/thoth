package snorri.semantics;

import snorri.nonterminals.Adverb;

public abstract class AdverbDef extends Definition {

	/**
	 * the getMeaning method for an adverb should return a modified SpellEvent
	 */
	
	public AdverbDef() {
		super(Adverb.class);
	}

}
