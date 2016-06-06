package snorri.nonterminals;

import java.util.List;

import snorri.parser.Node;

public abstract class NonTerminal implements Node {

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
