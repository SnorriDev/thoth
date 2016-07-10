package snorri.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComponent;



public class FullInventory extends ArrayList<Droppable> implements Serializable {

	/**
	 * class that represents a player's full inventory
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * will first try to insert items in a new inventory slot
	 * if this cannot be done, will attempt to stack them in an existing slot
	 * under this system, all stackable items (consumables mostly) should have maxStack == 1
	 */
	@Override
	public boolean add(Droppable drop) {
		int freq = Collections.frequency(this, drop);
		if (freq < drop.getMaxQuantity()) {
			super.add(drop);
			return true;
		}
		for (Droppable d : this) { //will only stack on the first one that it finds
			if (d.equals(drop) && d.stack(drop)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * whether a player has unlocked a word
	 * used to stop players from enchanting with vocabulary they haven't learned yet
	 * @param word
	 * 	the word of interest represented as a string
	 */
	public boolean knowsWord(String word) {
		return contains(new VocabDrop(word));
	}

	/**
	 * paint the full inventory at a specified component
	 * @param component
	 * 		the target JComponent which will be the inventory interface
	 */
	public void paint(JComponent component) {
		//TODO write this
	}
		
	//TODO Toby do stuff here!!

}
