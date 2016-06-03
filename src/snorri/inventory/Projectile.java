package snorri.inventory;

import java.awt.Image;

public class Projectile extends Item {	
	
	public Projectile(ItemType t) {
		super(t);
	}
	
	@Override
	public Image getBorder(boolean selected) {
		return selected ? PROJECTILE_BORDER_SELECTED : PROJECTILE_BORDER;
	}

}
