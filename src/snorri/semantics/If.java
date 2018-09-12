package snorri.semantics;

import snorri.events.CastEvent;
import snorri.nonterminals.Conditional;
import snorri.parser.Node;

public class If extends ConnectiveDef {

	public If() {
		super(Conditional.class);
	}

	@Override
	public boolean exec(Node<Boolean> arg1, Node<Boolean> arg2, CastEvent e) {
		if ((boolean) arg2.getMeaning(e)) {
			return (boolean) arg1.getMeaning(e);
		}
		return false;
	}

	@Override
	public boolean eval(boolean arg1, boolean arg2, CastEvent e) {
		return !arg2 || arg1;
	}
	
	@Override
	public String toString() {
		return "if";
	}

}
