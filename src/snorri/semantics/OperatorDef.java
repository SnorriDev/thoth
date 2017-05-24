package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.SemiTerminal;

abstract class OperatorDef extends Definition {

	protected SpellEvent e;
	
	protected OperatorDef(Class<? extends SemiTerminal> partOfSpeech) {
		super(partOfSpeech);
	}

	@Override
	public Object getMeaning(SpellEvent e) {
		this.e = e; //don't modify this
		return this;
	}

}
