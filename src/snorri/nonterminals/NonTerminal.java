package snorri.nonterminals;

import java.util.ArrayList;
import java.util.List;

import snorri.events.SpellEvent;
import snorri.main.Main;
import snorri.parser.Node;
import snorri.semantics.Category;

public abstract class NonTerminal<S> implements Node<S> {

	private static final long serialVersionUID = 1L;

	protected List<Node<?>> children;
	protected Category category;
	
	public void setChildren(List<Node<?>> nodes) {
		children = nodes;
		//FIXME assign category
	}
		
	//TODO nonsyncategorematic semantics
	//TODO replace trinary rules with binary branching structure?
	//TODO choose exec/eval based on something in SpellEvent
	@Override @SuppressWarnings("unchecked")
	public S getMeaning(SpellEvent e) {
		
		switch(children.size()) {
		
		case 1:
			return (S) children.get(0).getMeaning(e);
		case 2:
			
		}
		
		return null;
		
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
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
	
	@Override
	public Category getCategory() {
		return category;
	}
	
}
