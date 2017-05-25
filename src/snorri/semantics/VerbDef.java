package snorri.semantics;

import snorri.nonterminals.SemiTerminal;

public abstract class VerbDef<S> extends Definition<S> {

	protected VerbDef(Class<? extends SemiTerminal<?>> partOfSpeech, Category category) {
		super(partOfSpeech, category);
	}

}
