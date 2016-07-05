package snorri.inventory;

import java.io.Serializable;
import java.util.ArrayList;

import snorri.main.Main;

public class FullInventory extends ArrayList<Item> implements Serializable {

	/**
	 * Class that represents a player's full inventory
	 * Not much to it, since it basically just inherits everything from ArrayList
	 * Should be filterable (ask Will for enchanting interface design)
	 */
	private static final long serialVersionUID = 1L;

	public void unlock(Item drop) {
		//TODO figure out how to do unlocks; how to store items/duplicates?
	}
	
	//TODO Toby do stuff here!!

}
