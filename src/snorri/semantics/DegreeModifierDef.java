package snorri.semantics;

import snorri.events.CastEvent;
import snorri.nonterminals.DegreeModifier;

public class DegreeModifierDef extends Definition<Integer> {

	private final int degree;
	
	public DegreeModifierDef(int degree) {
		super(DegreeModifier.class, Integer.class);
		this.degree = degree;
	}

	@Override
	public Integer getMeaning(CastEvent e) {
		return degree;
	}

	@Override
	public String toString() {
		return "degree: " + degree;
	}

}
