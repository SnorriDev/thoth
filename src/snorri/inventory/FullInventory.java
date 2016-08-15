package snorri.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import snorri.nonterminals.Name;
import snorri.overlay.SortedListModel;
import snorri.parser.Lexicon;

public class FullInventory extends ArrayList<Droppable> implements Serializable {

	/**
	 * class that represents a player's full inventory
	 */
	private static final long serialVersionUID = 1L;
	
	private final Inventory inv;
	
	public FullInventory(Inventory inv) {
		this.inv = inv;
	}
	
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
		return contains(new VocabDrop(word)) || Name.class.equals(Lexicon.lookup(word).getPOS());
	}
	
	/**
	 * @see knowsWord
	 */
	public boolean knowsWords(List<String> words) {
		for (String word : words) {
			if (!knowsWord(word)) {
				return false;
			}
		}
		return true;
	}

	public SortedListModel<Item> getItemModel() {
		SortedListModel<Item> out = new SortedListModel<>();
		for (Droppable d : this) {
			if (d instanceof Item) {
				out.addElement((Item) d);
			}
		}
		return out;
	}
	
	public Item[] getItems() {
		return (Item[]) getArray(new Item[0]);
	}
	
	public VocabDrop[] getVocab() {
		VocabDrop[] res = (VocabDrop[]) getArray(new VocabDrop[0]);
		Arrays.sort(res);
		return res;
	}
	
	public Droppable[] getArray(Droppable[] arr) {
		List<Droppable> list = new ArrayList<>(); 
		for (Droppable d : this) {
			if (arr.getClass().getComponentType().isInstance(d)) {
				list.add((Droppable) d);
			}
		}
		return list.toArray(arr);
	}
	
	public Inventory getInventory() {
		return inv;
	}
		
}
