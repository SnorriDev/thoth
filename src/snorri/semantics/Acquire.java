package snorri.semantics;

import snorri.entities.Drop;
import snorri.events.SpellEvent;
import snorri.inventory.Carrier;
import snorri.inventory.Droppable;
import snorri.parser.Node;

public class Acquire extends TransVerbDef {

	@Override
	public boolean eval(Object subject, Object object, SpellEvent e) {
		
		if (!(subject instanceof Carrier)) {
			return false;
		}
		
		return ((Carrier) subject).getFullInventory().contains(object);
		
	}

	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		
		if (object instanceof Droppable) {
			e.getFirstPerson().getInventory().add((Droppable) object);
		}
		
		//maybe we shouldn't do this; force players to say "prize of it"
		if (object instanceof Drop) {
			e.getFirstPerson().getInventory().add(((Drop) object).getPrize());
		}
		
		return false;
		
	}

	@Override
	public String toString() {
		return "acquire";
	}

}
