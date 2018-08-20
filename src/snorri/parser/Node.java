package snorri.parser;

import snorri.events.SpellEvent;

public interface Node<S> {
	
	/**
	 * @param e
	 * 	a <code>SpellEvent</code> represented some context
	 * @return
	 * 	the meaning of the linguistic expression represented by this node with respect to <code>e</code>
	 * S is the semantic type of this node.
	 */
	public S getMeaning(SpellEvent e);

	public boolean altersMovement();
	
	public String getOrthography();
	
	public Node<S> copy();
	
	public void computeCategory();
	
	public Object getCategory();

	/**
	 * @return The first word of a spell, or null if it is empty.
	 */
	default String getFirstWord() {
		String[] words = getOrthography().split(" ");
		if (words.length == 0) {
			return null;
		}
		return words[0];
	}
		
}
