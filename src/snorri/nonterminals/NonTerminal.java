package snorri.nonterminals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import snorri.events.SpellEvent;
import snorri.main.Debug;
import snorri.parser.Node;
import snorri.semantics.Category;

public abstract class NonTerminal<S> implements Node<S> {

	protected List<Node<?>> children;
	protected Object category;
	
	//TODO backwards compatible implementation with a list and an arraylist
	
	//TODO replace trinary rules with binary branching structure?
	//choose exec/eval based on something in SpellEvent
	public void setChildren(List<Node<?>> nodes) {
		children = nodes;
		category = Category.combine(nodes);
	}
	
	public void computeCategory() {
		for (Node<?> child : children) {
			child.computeCategory();
		}
		category = Category.combine(children);
	}
		
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
			Debug.logger.log(Level.SEVERE, "Failed to copy NonTerminal.", e);
			return null;
		}

	}
	
	@Override
	public Object getCategory() {
		return category;
	}
	
}
