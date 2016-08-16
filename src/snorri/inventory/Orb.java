package snorri.inventory;

import java.awt.Color;

public class Orb extends Item {	
	
	private static final long serialVersionUID = 1L;
	
	private static final Color ORB_COOLDOWN_COLOR = new Color(118, 45, 50, 150);
	
	public Orb(ItemType t) {
		super(t);
	}
	
	@Override
	public Color getArcColor() {
		return ORB_COOLDOWN_COLOR;
	}
	
	@Override
	public int getInvPos() {
		return 2;
	}

}
