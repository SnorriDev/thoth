package snorri.inventory;

import snorri.parser.Node;



public class EmptyInvSlot extends Item {
	public EmptyInvSlot() {
		id = 0;
		quantity = 1;
		spell = null;
	}
	
	public EmptyInvSlot(int itemID) {
		id = itemID;
		quantity = 1;
		spell = null;
	}
	
	public EmptyInvSlot(int itemID, int amount) {
		id = itemID;
		quantity = 1;
		spell = null;
	}
	
	public EmptyInvSlot(int itemID, Node enchantment) {
		id = itemID;
		quantity = 1;
		spell = null;
	}
	
	public EmptyInvSlot(int itemID, int amount, Node enchantment) {
		id = itemID;
		quantity = 1;
		spell = null;
	}
}
