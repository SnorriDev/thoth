package snorri.parser;

import java.util.List;

public class Rule {

	private Object[] specs;
	private Class<? extends NonTerminal> c;
	
	//TODO: add semantics associated with rules
	
	//nodes is a list of Strings and NonTerminals
	public Rule(Object[] specs, Class<? extends NonTerminal> c) {
		this.specs = specs;
		this.c = c;
	}
	
	public Class<? extends NonTerminal> fits(List<Node> nodes) {
		if (nodes.size() != specs.length)
			return null;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) instanceof Terminal && specs[i] instanceof String) {
				if (! ((Terminal) nodes.get(i)).equals((String) specs[i]))
					return null;
			}
			else if (! nodes.get(i).getClass().equals(specs[i]))
				return null;
		}
		return c;
	}
	
	
	
}
