package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.Conditional;

public class ConditionalDef extends Definition {

	public ConditionalDef() {
		super(Conditional.class);
	}

	@Override
	public Object getMeaning(SpellEvent e) {
		return null;
	}
	
	@Override
	public String getShortDesc() {
		return "if";
	}

}
