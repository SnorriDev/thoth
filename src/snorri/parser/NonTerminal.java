package snorri.parser;

import java.util.List;

public abstract class NonTerminal implements Node {

	private List<Node> children;
	public void setChildren(List<Node> children) {
		this.children = children;
	}
		
	//TODO: probably remove lambdables and stuff
	//save lambdables for the meaning of terminals, but just use functions here
	
	public String getString() {
		return getClass().getName();
	}

	@Deprecated
	public List<Node> getChildren() {
		return children;
	}
	
}
