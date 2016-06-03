package snorri.inventory;

import java.awt.Image;

public class Projectile extends Item {	
	
	public Projectile(ItemType t) {
		super(t);
	}
	
	@Override
	public Image getBorder() {
		return PROJECTILE_BORDER;
	}

}
