package snorri.parser;

import java.io.Serializable;

import snorri.events.SpellEvent;

public interface Node extends Serializable {

	//FIXME add abstract classes Unary, Binary, and TernaryNode with generics
	
	/**
	 * @param e
	 * 	a <code>SpellEvent</code> represented some context
	 * @return
	 * 	the meaning of the linguistic expression represented by this node with respect to <code>e</code>
	 */
	public Object getMeaning(SpellEvent e);

	public boolean altersMovement();
	
	public String getOrthography();
	
	public Node copy();
		
}
