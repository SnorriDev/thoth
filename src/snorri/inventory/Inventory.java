package snorri.inventory;

import java.awt.Color;
import java.awt.Graphics;

import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Vector;

public class Inventory { // TODO: implement cooldown
	
	private Weapon weaponSlot;
	private Armor armorSlot;
	private Projectile[] projectileSlots;
	private Papyrus[] papyrusSlots;

	private static final int PROJECTILE_SLOTS = 2;
	private static final int PAPYRUS_SLOTS = 3;
	
	private static final int SLOT_WIDTH = 64;
	private static final int BOTTOM_MARGIN = 20;
	private static final int SLOT_SPACE = 15;
	
	private static final Color BORDER = new Color(127, 130, 99);
	private static final Color BACKGROUND = new Color(206, 176, 103, 200);

	public Inventory() {
		projectileSlots = new Projectile[PROJECTILE_SLOTS];
		papyrusSlots = new Papyrus[PAPYRUS_SLOTS];
	}

	public void addProjectile(Projectile newProjectile) {
		for (int i = 0; i < PROJECTILE_SLOTS; i++) {
			if (projectileSlots[i] == null) {
				projectileSlots[i] = newProjectile;
				return;
			}
		}
		Main.error("all projectile slots full, cannot add projectile");
	}

	public void addPapyrus(Papyrus newPapyrus) {
		for (int i = 0; i < PAPYRUS_SLOTS; i++) {
			if (papyrusSlots[i] == null) {
				papyrusSlots[i] = newPapyrus;
				return;
			}
		}
		Main.error("all papyrus slots full, cannot add papyrus");
	}

	public Weapon getWeapon() {
		return weaponSlot;
	}

	// if you want to set the slot to empty, pass null
	public void setWeapon(Weapon newWeapon) {
		weaponSlot = newWeapon;
		return;
	}

	public Armor getArmor() {
		return armorSlot;
	}

	public void setArmor(Armor newArmor) {
		armorSlot = newArmor;
	}

	public Projectile getProjectile(int index) {
		if (index < 0 || index >= PROJECTILE_SLOTS) {
			Main.error("index out of range, returning empty");
			return null;
		}
		return projectileSlots[index];
	}

	public void setProjectile(int slot, Projectile newProjectile) {
		if (slot < 0 || slot >= PROJECTILE_SLOTS) {
			Main.error("slot out of range");
			return;
		}
		projectileSlots[slot] = newProjectile;
		return;
	}

	public Papyrus getPapyrus(int index) {
		if (index < 0 || index >= PAPYRUS_SLOTS) {
			Main.error("index out of range, returning empty");
			return null;
		}
		return papyrusSlots[index];
	}

	public void setPapyrus(int slot, Papyrus newPapyrus) {
		if (slot < 0 || slot >= PAPYRUS_SLOTS) {
			Main.error("slot out of range");
			return;
		}
		papyrusSlots[slot] = newPapyrus;
		return;
	}
	
	public void render(GameWindow window, Graphics g) {
		
		Vector dim = window.getDimensions();
		int x = (dim.getX() - 5 * SLOT_WIDTH) / 2 - SLOT_SPACE;
		int y = dim.getY() - BOTTOM_MARGIN - SLOT_WIDTH;
		
		drawSmallContainers(g, new Vector(x, y), weaponSlot, armorSlot);
		x += SLOT_WIDTH / 2;
		
		for (int i = 0; i < PROJECTILE_SLOTS; i++) {
			drawItemContainer(g, new Vector(x, y), projectileSlots[i]);
			x += SLOT_WIDTH;
		}
		x += SLOT_SPACE;
		
		for (int i = 0; i < PAPYRUS_SLOTS; i++) {
			drawItemContainer(g, new Vector(x, y), papyrusSlots[i]);
			x += SLOT_WIDTH;
		}
		
	}
	
	private void drawSmallContainers(Graphics g, Vector pos, Item item1, Item item2) {
		
		g.setColor(BACKGROUND);
		g.fillRect(pos.getX(), pos.getY(), SLOT_WIDTH / 2, SLOT_WIDTH);
		
		g.setColor(BORDER);
		g.drawRect(pos.getX(), pos.getY(), SLOT_WIDTH / 2, SLOT_WIDTH / 2);
		g.drawRect(pos.getX(), pos.getY() + SLOT_WIDTH / 2, SLOT_WIDTH / 2, SLOT_WIDTH / 2);
	
		if (item1 != null) {
			item1.renderSmall(g, pos);
		}
		
		if (item2 != null) {
			item2.renderSmall(g, pos);
		}
		
	}
	
	private void drawItemContainer(Graphics g, Vector pos, Item item) {
		
		g.setColor(BACKGROUND);
		g.fillRect(pos.getX(), pos.getY(), SLOT_WIDTH, SLOT_WIDTH);

		//TODO: once all Item textures are non-null, maybe move this to Item when we render the image
		g.setColor(BORDER);
		g.drawRect(pos.getX(), pos.getY(), SLOT_WIDTH, SLOT_WIDTH);
		
		g.setColor(Color.BLACK);
		if (item != null) {
			item.render(g, pos);
		}
		
	}

}