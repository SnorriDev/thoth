package snorri.parser;

import java.io.Serializable;

import snorri.events.SpellEvent;

public interface Node<S> extends Serializable {

	//FIXME add abstract classes Unary, Binary, and TernaryNode with generics
	//S is the semantic type of this node
	
	/**
	 * @param e
	 * 	a <code>SpellEvent</code> represented some context
	 * @return
	 * 	the meaning of the linguistic expression represented by this node with respect to <code>e</code>
	 */
	public S getMeaning(SpellEvent e);

	public boolean altersMovement();
	
	public String getOrthography();
	
	public Node<S> copy();
	
	public void computeCategory();
	
	public Object getCategory();
		
}
