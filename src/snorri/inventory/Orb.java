package snorri.inventory;

import java.awt.Color;

import snorri.animations.Animation;

public class Orb extends Item {	
	
	private static final long serialVersionUID = 1L;
	
	private static final Color ORB_COOLDOWN_COLOR = new Color(1, 69, 101, 150);
	
	public Orb(ItemType t) {
		super(t);
	}
	
	@Override
	public Color getArcColor() {
		return ORB_COOLDOWN_COLOR;
	}

	public Animation getProjectileAnimation() {
		return (Animation) type.getProperty(0);
	}

}