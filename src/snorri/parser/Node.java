package snorri.parser;

import java.io.Serializable;

import snorri.events.SpellEvent;

public interface Node extends Serializable {

	public Object getMeaning(SpellEvent e);

	public boolean altersMovement();
	
	public String getOrthography();
		
}
