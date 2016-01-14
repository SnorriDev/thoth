package snorri.parser;

import java.util.List;

public class Rule {

	private Object[] specs;
	private Class<NonTerminal> c;
	
	//TODO: add semantics associated with rules
	
	//nodes is a list of Strings and NonTerminals
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Rule(Object[] specs, Class c) {
		this.specs = specs;
		this.c = c;
	}
	
	@SuppressWarnings("rawtypes")
	public Class fits(List<Node> nodes) {
		if (nodes.size() != specs.length)
			return null;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) instanceof Terminal) {
				if (! nodes.get(i).equals(specs[i]))
					return null;
			}
			else if (! nodes.get(i).getClass().equals(specs[i]))
				return null;
		}
		return c;
	}
	
	
	
}
