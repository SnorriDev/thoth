package snorri.inventory;

import java.io.Serializable;

import snorri.inventory.Item.ItemType;
import snorri.parser.Lexicon;

public interface Droppable extends Serializable {
	
	public static Droppable fromString(String raw) {
		ItemType type = ItemType.fromString(raw);
		if (type != null) {
			return type.getNew();
		}
		if (Lexicon.lookup(raw) != null) {
			return new VocabDrop(raw);
		}
		return null;
	}
	
	public int getMaxQuantity();
	
	//TODO use this method to update charges on consumables, etc.
	public boolean stack(Droppable other);
	
}
