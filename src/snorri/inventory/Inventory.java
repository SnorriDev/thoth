package snorri.inventory;

import java.awt.Graphics;

import snorri.entities.Player;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Vector;

public class Inventory {
	
	private Player player;
	
	private Weapon weaponSlot;
	private Armor armorSlot;
	private Projectile[] projectileSlots;
	private Papyrus[] papyrusSlots;
	private int selectedProjectile = 0;

	private static final int PROJECTILE_SLOTS = 2;
	private static final int PAPYRUS_SLOTS = 3;
	
	private static final int MARGIN = 20;
	private static final int SLOT_SPACE = 15;

	public Inventory(Player player) {
		this.player = player;
		projectileSlots = new Projectile[PROJECTILE_SLOTS];
		papyrusSlots = new Papyrus[PAPYRUS_SLOTS];
	}
	
	public void update(float deltaTime) {
		//TODO: cooldowns on other things as well?
		for (int i = 0; i < PAPYRUS_SLOTS; i++) {
			if (papyrusSlots[i] != null) {
				papyrusSlots[i].updateCooldown(deltaTime);
			}
		}
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
	
	public void usePapyrus(int i) {
		if (papyrusSlots[i] != null) {
			papyrusSlots[i].tryToActivate(player);
		}
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
			//will draw as selected/not selected based on cooldown
			drawItemContainer(g, topPos, papyrusSlots[i], Papyrus.class, !(papyrusSlots[i] == null || !papyrusSlots[i].canUse()));
		}
		
		Vector bottomPos = new Vector(MARGIN, window.getDimensions().getY() - MARGIN - Item.getSlotWidth());
		
		drawItemContainer(g, bottomPos, weaponSlot, Weapon.class, true);
		drawItemContainer(g, bottomPos, armorSlot, Armor.class, true);
		
	}
	
	//might be nicer if we split this into three different methods
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
			width = item.drawThumbnail(g, pos, flag);
		}
		
		pos.add(width + SLOT_SPACE, 0);
		
	}

}