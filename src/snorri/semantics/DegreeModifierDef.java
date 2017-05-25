package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.nonterminals.DegreeModifier;

public class DegreeModifierDef extends Definition<Integer> {

	private final int degree;
	
	public DegreeModifierDef(int degree) {
		super(DegreeModifier.class);
		this.degree = degree;
	}

	@Override
	public Integer getMeaning(SpellEvent e) {
		return degree;
	}

	@Override
	public String toString() {
		return "degree: " + degree;
	}

}
