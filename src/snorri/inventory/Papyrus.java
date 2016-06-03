package snorri.inventory;

import java.awt.Image;

public class Papyrus extends Item {

	public Papyrus(ItemType t) {
		super(t);
	}
	
	@Override
	public Image getBorder() {
		return PAPYRUS_BORDER;
	}

}
