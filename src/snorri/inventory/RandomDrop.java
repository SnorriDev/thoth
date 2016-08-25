package snorri.inventory;

import java.awt.Image;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import snorri.main.Main;
import snorri.main.Util;

public class RandomDrop implements Droppable {

	private static final long serialVersionUID = 1L;
	
	private static final Map<String, Collection<Droppable>> TIERS;
	
	static {
		TIERS = new HashMap<String, Collection<Droppable>>();
		
		TIERS.put("COMMON", new HashSet<>());
		TIERS.get("COMMON").add(new VocabDrop("nb"));
		TIERS.get("COMMON").add(new VocabDrop("nbt"));
				
	}
	
	private final Collection<Droppable> col;

	public RandomDrop(Collection<Droppable> col) {
		this.col = col;
	}
	
	public RandomDrop(String tier) {
		this.col = TIERS.get(tier.toUpperCase());
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
		Main.log(TIERS.size() + " drop tiers loaded");
	}

}
