package snorri.parser;

import java.util.ArrayList;

import snorri.nonterminals.SemiTerminal;
import snorri.semantics.Definition;

public class DefinitionList extends ArrayList<Definition<?>> {

	//TODO: use this to store multiple definitions?
	
	private static final long serialVersionUID = 1L;
	
	public Definition<?> get(Class<? extends SemiTerminal<?>> c) {
		
		for (Definition<?> d : this) {
			if (d.isPOS(c)) {
				return d;
			}
		}
		
		return get();
		
	}

	public Definition<?> get() {
		return get(0);
	}
	
}
