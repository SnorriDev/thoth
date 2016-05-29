package snorri.inventory;

import snorri.parser.Node;



public class Weapon extends Item {
	public Weapon() {
		id = 3;
		quantity = 1;
		spell = null;
	}
	
	public Weapon(int itemID) {
		id = itemID;
		quantity = 1;
		spell = null;
	}
	
	public Weapon(int itemID, int amount) {
		id = itemID;
		quantity = amount;
		spell = null;
		if (quantity > MAXQUANTITY[id]) {
			quantity = MAXQUANTITY[id];
		}
		if (quantity <= 1) {
			id = 0;
			quantity = 1;
			spell = null;
		}
	}
	
	public Weapon(int itemID, Node enchantment) {
		id = itemID;
		quantity = 1;
		if (ENCHANTABLE[id])
			spell = enchantment;
		else
			spell = null;
	}
	
	public Weapon(int itemID, int amount, Node enchantment) {
		id = itemID;
		quantity = amount;
		if (ENCHANTABLE[id])
			spell = enchantment;
		else
			spell = null;
		if (quantity > MAXQUANTITY[id]) {
			quantity = MAXQUANTITY[id];
		}
		if (quantity <= 1) {
			id = 0;
			quantity = 1;
			spell = null;
		}
	}
}
