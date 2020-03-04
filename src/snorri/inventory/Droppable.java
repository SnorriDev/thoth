package snorri.inventory;

import java.awt.Image;

import snorri.animations.Animation;
import snorri.parser.Grammar;
import snorri.semantics.nouns.Nominal;

public interface Droppable extends Nominal {
	
	public static final Animation SPARKLE = new Animation("/textures/animations/sparkle");
	
	public static Droppable fromString(String raw) {
		Droppable result;
		if ((result = RandomDrop.fromString(raw)) != null) {
			return result;
		}
		if ((result = PapyrusDrop.fromString(raw)) != null) {
			return result;
		}
		if ((result = Item.fromString(raw)) != null) {
			return result;
		}
		if ((result = VocabDrop.fromString(raw)) != null) {
			return result;
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
