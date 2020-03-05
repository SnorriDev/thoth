package snorri.inventory;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import snorri.grammar.Lexicon;
import snorri.main.Debug;
import snorri.util.Util;

public class RandomDrop implements Droppable {

	private static final long serialVersionUID = 1L;
			
	public static enum Tier {
		
		COMMON,
		RARE,
		EPIC,
		LEGENDARY,
		ALL(Lexicon.getDropsInLang());
		
		private Collection<Droppable> col;
		
		Tier(Collection<Droppable> col) {
			this.col = col;
		}
		
		Tier(Droppable[] arr) {
			this(Arrays.asList(arr));
		}
		
		Tier() {
			this.col = new ArrayList<>();
		}
		
		public Collection<Droppable> getCollection() {
			return col;
		}

		/**
		 * Add a new item to this drop tier.
		 * This is not guaranteed to work if the drop tier was constructed from an arbitrary collection.
		 * @param d
		 * 	The <code>Droppable</code> item to add
		 */
		public void add(Droppable d) {
			col.add(d);
		}
		
		public void add(String key) {
			add(new VocabDrop(key));
		}
		
	}
	
	private final Collection<Droppable> col;
	private String name;

	public RandomDrop(Collection<Droppable> col) {
		this.col = col;
	}
	
	public RandomDrop(Tier tier) {
		this(tier.getCollection());
	}
	
	public RandomDrop(String name) {
		this(Tier.valueOf(name.toUpperCase()));
		this.name = name.toUpperCase();
	}

	@Override
	public int getMaxQuantity() {
		return 1;
	}

	@Override
	public boolean stack(Droppable other) {
		return false;
	}

	@Override
	public Image getTexture() {
		return null;
	}
	
	public Droppable getDroppable() {
		return Util.random(col).copy();
	}

	@Override
	public Droppable copy() {
		return new RandomDrop(col);
	}

	public static void load() {
		Debug.logger.info(Tier.values().length + " drop tiers loaded.");
	}
	
	@Override
	public String toString() {
		return "!" + name;
	}

	public static Droppable fromString(String raw) {
		if (raw.startsWith("!")) {
			return new RandomDrop(raw.substring(1));
		}
		return null;
	}

}
