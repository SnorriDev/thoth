package snorri.semantics;

import snorri.nonterminals.Conjunction;
import snorri.parser.Node;

public class And extends ConnectiveDef {

	public And() {
		super(Conjunction.class);
	}

	@Override
	public boolean exec(Node arg1, Node arg2) {
		return (boolean) arg1.getMeaning(e) && (boolean) arg2.getMeaning(e);
	}

	@Override
	public boolean eval(boolean arg1, boolean arg2) {
		return arg1 && arg2;
	}

	@Override
	public String toString() {
		return "and";
	}

}
