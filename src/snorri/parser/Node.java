package snorri.parser;

import snorri.events.SpellEvent;

public interface Node {

	public Object getMeaning(SpellEvent e);

	public boolean altersMovement();
		
}
