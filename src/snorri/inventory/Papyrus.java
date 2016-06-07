package snorri.inventory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import snorri.entities.Player;
import snorri.main.Main;
import snorri.world.Vector;

public class Papyrus extends Item {

	private static final long serialVersionUID = 1L;
	
	private static final Image PAPYRUS_BORDER = Main.getImageResource("/textures/hud/papyrusBorderInactive.png");
	private static final Image PAPYRUS_BORDER_SELECTED = Main.getImageResource("/textures/hud/papyrusBorder.png");
	private static final Color PAPYRUS_COOLDOWN_COLOR = new Color(118, 45, 50, 150);
	
	public Papyrus(ItemType t) {
		super(t);
		timer = new Timer(5);
	}
	
	public boolean tryToActivate(Player player) {
		
		if (timer.activate()) {
			Main.log("spell output: " + useSpellOn(player));
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public Color getCooldownColor() {
		return PAPYRUS_COOLDOWN_COLOR;
	}
	
	@Override
	public Image getBorder(boolean selected) {
		return selected ? PAPYRUS_BORDER_SELECTED : PAPYRUS_BORDER;
	}
	
	public static int drawEmptyPapyrus(Graphics g, Vector pos, boolean selected) {
		g.drawImage(selected ? PAPYRUS_BORDER_SELECTED : PAPYRUS_BORDER, pos.getX(), pos.getY(), null);
		return getSlotWidth();
	}

}
