package snorri.inventory;

import java.awt.Image;

import snorri.inventory.Item.ItemType;

public class PapyrusDrop implements Droppable {

	private static final long serialVersionUID = 1L;
	private static final String SHORTHAND = "papyrus:";
	
	private final int quantity;
	
	public PapyrusDrop(int quantity) {
		this.quantity = quantity;
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
		return ItemType.PAPYRUS.getTexture();
	}

	@Override
	public Droppable copy() {
		return new PapyrusDrop(quantity);
	}
	
	@Override
	public String toString() {
		return SHORTHAND + quantity;
	}
	
	public static PapyrusDrop fromString(String raw) {
		if (raw.toLowerCase().startsWith(SHORTHAND)) {
			return new PapyrusDrop(Integer.parseInt(raw.substring(SHORTHAND.length())));
		}
		return null;
	}
	
	public int getQuantity() {
		return quantity;
	}

}
