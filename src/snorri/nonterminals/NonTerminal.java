package snorri.nonterminals;

import java.util.ArrayList;
import java.util.List;

import snorri.parser.Node;

public abstract class NonTerminal implements Node {

	private static final long serialVersionUID = 1L;

	protected List<Node> children;
	
	public void setChildren(List<Node> children) {
		this.children = children;
	}
		
	//TODO: probably remove lambdables and stuff
	//save lambdables for the meaning of terminals, but just use functions here
	
	@Override
	public String toString() {
		//TODO: traverse full sentence
		//java 8 makes this nice lol
		return getClass().getSimpleName();
	}
	
	//TODO override this in special cases with equal signs
	@Override
	public String getOrthography() {
		List<String> strings = new ArrayList<String>();
		for (Node child : children) {
			strings.add(child.getOrthography());
		}
		return String.join(" ", strings);
	}

	@Deprecated
	public List<Node> getChildren() {
		return children;
	}
	
	@Override
	public boolean altersMovement() {
		
		for (Node child : children) {
			if (child.altersMovement()) {
				return true;
			}
		}
		
		return false;
	}
	
}
