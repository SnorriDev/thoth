package snorri.nonterminals;

import java.util.ArrayList;
import java.util.List;

import snorri.main.Main;
import snorri.parser.Node;

public abstract class NonTerminal<S> implements Node<S> {

	private static final long serialVersionUID = 1L;

	protected List<Node<?>> children;
	
	public void setChildren(List<Node<?>> nodes) {
		this.children = nodes;
	}
		
	//TODO: probably remove lambdables and stuff
	//save lambdables for the meaning of terminals, but just use functions here
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	//TODO override this in special cases with equal signs
	@Override
	public String getOrthography() {
		List<String> strings = new ArrayList<String>();
		for (Node<?> child : children) {
			strings.add(child.getOrthography());
		}
		return String.join(" ", strings);
	}

	@Deprecated
	public List<Node<?>> getChildren() {
		return children;
	}
	
	@Override
	public boolean altersMovement() {
		
		for (Node<?> child : children) {
			if (child.altersMovement()) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override @SuppressWarnings("unchecked")
	public NonTerminal<S> copy() {
		List<Node<?>> newChildren = new ArrayList<>();
		for (Node<?> child : children) {
			newChildren.add(child.copy());
		}
		NonTerminal<S> copy;
		try {
			copy = (NonTerminal<S>) getClass().newInstance();
			copy.setChildren(newChildren);
			return copy;
		} catch (InstantiationException | IllegalAccessException e) {
			Main.error("could not copy NonTerminal");
			e.printStackTrace();
			return null;
		}

	}
	
}
