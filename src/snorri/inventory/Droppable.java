package snorri.inventory;

import java.awt.Image;
import java.io.Serializable;

import snorri.animations.Animation;
import snorri.inventory.Item.ItemType;
import snorri.parser.Lexicon;

public interface Droppable extends Serializable, Comparable<Droppable> {
	
	public static final Animation SPARKLE = new Animation("/textures/animations/sparkle");
	
	static class CompareWrapper {
		
		/**
		 * Need to put the inventory for droppable comparison
		 * in a static class 
		 */
		
		private static Inventory inv;
		
	}
	
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
	
	//use this method to update charges on consumables, etc.
	public boolean stack(Droppable other);

	public Image getTexture();

	default Animation getAnimation() {
		return new Animation(SPARKLE);
	}
	
	/**
	 * For sorting the full inventory in HUD
	 */
	default int getInvPos() {
		return 10;
	}
	
	/**
	 * This MUST be overriden to resolve cases where the
	 * inventory positions are equal.
	 */
	default int compareIn(Droppable other, Inventory inv) {
		return Integer.compare(getInvPos(), other.getInvPos());
	}
	
	@Override
	default int compareTo(Droppable other) {
		return compareIn(other, CompareWrapper.inv);
	}
	
	public static void setInventoryForComparison(Inventory inv) {
		CompareWrapper.inv = inv;
	}
	
	default String toUniqueString() {
		return toString() + hashCode();
	}
	
}
