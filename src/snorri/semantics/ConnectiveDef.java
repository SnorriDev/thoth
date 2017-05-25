package snorri.semantics;

import snorri.nonterminals.Connective;
import snorri.parser.Node;

public abstract class ConnectiveDef extends OperatorDef {
		
	public ConnectiveDef(Class<? extends Connective> c) {
		super(c);
	}
		
	/**
	 * Execute two connected commands
	 * @param arg1 The first argument to the connective
	 * @param arg2 The second argument to the connective
	 * @return Whether or not anything executed correctly
	 */
	public abstract boolean exec(Node arg1, Node arg2);

	/**
	 * Evaluate the truth value with two truth valued arguments
	 * @param arg1 the first argument to the connective
	 * @param arg2 the second argument to the connective
	 * @return A truth value
	 */
	public abstract boolean eval(boolean arg1, boolean arg2);

}