package snorri.inventory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import snorri.main.Main;
import snorri.world.Vector;

public class Orb extends Item {	
	
	private static final long serialVersionUID = 1L;
	
	private static final Image ORB_BORDER = Main.getImage("/textures/hud/projectileBorderInactive.png");
	private static final Image ORB_BORDER_SELECTED = Main.getImage("/textures/hud/projectileBorder.png");
	private static final Color ORB_COOLDOWN_COLOR = new Color(1, 69, 101, 150);
	
	public Orb(ItemType t) {
		super(t);
	}
	
	@Override
	public Image getBorder(boolean selected) {
		return selected ? ORB_BORDER_SELECTED : ORB_BORDER;
	}
	
	@Override
	public Color getCooldownColor() {
		return ORB_COOLDOWN_COLOR;
	}
	
	public static int drawEmptyOrb(Graphics g, Vector pos, boolean selected) {
		g.drawImage(selected ? ORB_BORDER_SELECTED : ORB_BORDER, pos.getX(), pos.getY(), null);
		return getSlotWidth();
	}

}
