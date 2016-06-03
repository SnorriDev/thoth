package snorri.inventory;

import java.awt.Graphics;

import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Vector;

public class Inventory { // TODO: implement cooldown
	
	private Weapon weaponSlot;
	private Armor armorSlot;
	private Projectile[] projectileSlots;
	private Papyrus[] papyrusSlots;
	private int selectedProjectile = 0;

	private static final int PROJECTILE_SLOTS = 2;
	private static final int PAPYRUS_SLOTS = 3;
	
	private static final int MARGIN = 20;
	private static final int SLOT_SPACE = 15;

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
	
	public Projectile getSelectedProjectile() {
		return getProjectile(selectedProjectile);
	}
	
	public void selectProjectile(int i) {
		selectedProjectile = i;
	}

	
	public void render(GameWindow window, Graphics g) {
		
		Vector topPos = new Vector(MARGIN, MARGIN);
		
		for (int i = 0; i < PROJECTILE_SLOTS; i++) {
			drawItemContainer(g, topPos, projectileSlots[i], Projectile.class, selectedProjectile == i);
		}
		
		for (int i = 0; i < PAPYRUS_SLOTS; i++) {
			drawItemContainer(g, topPos, papyrusSlots[i], Papyrus.class, false);
		}
		
		Vector bottomPos = new Vector(MARGIN, window.getDimensions().getY() - MARGIN - Item.getSlotWidth());
		
		drawItemContainer(g, bottomPos, weaponSlot, Weapon.class, false);
		drawItemContainer(g, bottomPos, armorSlot, Armor.class, false);
		
	}
	
	//updates the vector
	private void drawItemContainer(Graphics g, Vector pos, Item item, Class<? extends Item> slotType, boolean flag) {
		
		int width;	
		if (item == null) {
			if (slotType.equals(Papyrus.class)) {
				width = Item.drawEmptyPapyrus(g, pos, flag);
			} else if (slotType.equals(Projectile.class)) {
				width = Item.drawEmptyProjectile(g, pos, flag);
			} else {
				width = Item.drawEmpty(g, pos);
			}
		} else {
			width = item.drawThumbnail(g, pos);
		}
		
		pos.add(width + SLOT_SPACE, 0);
		
	}

}