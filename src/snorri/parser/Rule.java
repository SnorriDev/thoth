package snorri.parser;

import java.util.List;

import snorri.nonterminals.NonTerminal;

public class Rule {

	private Object[] specs;
	private Class<? extends NonTerminal<?>> c;
	
	//TODO: add semantics associated with rules
	
	//nodes is a list of Strings and NonTerminals
	public Rule(Object[] specs, Class<? extends NonTerminal<?>> c) {
		this.specs = specs;
		this.c = c;
	}
	
	public Class<? extends NonTerminal<?>> fits(List<Node<?>> nodes) {
		if (nodes.size() != specs.length)
			return null;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) instanceof Terminal && specs[i] instanceof String) {
				if (! ((Terminal<?>) nodes.get(i)).equals((String) specs[i]))
					return null;
			}
			else if (! nodes.get(i).getClass().equals(specs[i]))
				return null;
		}
		return c;
	}

	public int getLength() {
		return specs.length;
	}

	public Class<? extends NonTerminal<?>> getRoot() {
		return c;
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends Node<?>> getNode(int i) {
		return (Class<? extends Node<?>>) getRewrite(i);
	}
	
	public Object getRewrite(int i) {
		return specs[i];
	}
	
	@Override
	public String toString() {
		String out = niceName(c) + " -> ";
		for (Object s : specs) {
			out += niceName(s) + " ";
		}
		return out;
	}
	
	private String niceName(Object o) {
		return (o instanceof Class<?>) ? ((Class<?>) o).getSimpleName() : o.toString();
	}
	
}
