package snorri.inventory;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import snorri.main.Main;
import snorri.main.Util;
import snorri.parser.Lexicon;

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
	
	public RandomDrop(String name) {
		this(Tier.valueOf(name.toUpperCase()).getCollection());
		this.name = name.toUpperCase();
	}
	
	@Override
	public int compareTo(Droppable o) {
		Main.error("tried to compare Droppable to RandomDrop");
		return 0;
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
		Main.log(Tier.values().length + " drop tiers loaded");
	}
	
	public String toString() {
		return "!" + name;
	}

}
