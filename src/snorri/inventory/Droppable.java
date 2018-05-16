package snorri.inventory;

import java.awt.Image;

import snorri.animations.Animation;
import snorri.inventory.Item.ItemType;
import snorri.parser.Grammar;
import snorri.parser.Lexicon;
import snorri.semantics.Nominal;

public interface Droppable extends Nominal {
	
	public static final Animation SPARKLE = new Animation("/textures/animations/sparkle");
	
	static class CompareWrapper {
		
		/**
		 * Need to put the inventory for Droppable comparison
		 * in a static class 
		 */
				
	}
	
	public static Droppable fromString(String raw) {
		if (raw.startsWith("!")) {
			return new RandomDrop(raw.substring(1));
		}
		ItemType type = ItemType.fromString(raw);
		if (type != null) {
			return type.getNew();
		}
		if (Lexicon.lookup(raw) != null) {
			return new VocabDrop(raw);
		}
		return null;
	}
	
	public static Droppable fromStringWithSpell(String prize, String spell) {
		Droppable d = Droppable.fromString(prize);
		if (d instanceof Item) {
			((Item) d).setSpell(Grammar.parseSentence(spell));
		}
		return d;
	}
	
	public int getMaxQuantity();
	
	//use this method to update charges on consumables, etc.
	public boolean stack(Droppable other);

	public Image getTexture();

	default Animation getAnimation() {
		return new Animation(SPARKLE);
	}
	
	default String toUniqueString() {
		return toString() + hashCode();
	}
	
	public Droppable copy();
	
}
