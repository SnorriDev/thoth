package snorri.inventory;

import java.awt.Graphics;
import java.io.Serializable;

import snorri.entities.Projectile;
import snorri.entities.Unit;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Inventory implements Serializable {
	
	/**
	 * Holds the inventory of a player
	 */
	private static final long serialVersionUID = 1L;

	private Unit player;
	
	private Weapon weaponSlot;
	private Armor armorSlot;
	private Orb[] orbSlots;
	private Papyrus[] papyrusSlots;
	private int selectedOrb = 0;

	private static final int ORB_SLOTS = 2;
	private static final int PAPYRUS_SLOTS = 3;
	
	private static final int SLOT_SPACE = 15;

	public Inventory(Unit player) {
		this.player = player;
		orbSlots = new Orb[ORB_SLOTS];
		papyrusSlots = new Papyrus[PAPYRUS_SLOTS];
	}
	
	public void update(float deltaTime) {

		if (weaponSlot != null) {
			weaponSlot.updateCooldown(deltaTime);
		}
		
		if (armorSlot != null) {
			armorSlot.updateCooldown(deltaTime);
		}
		
		for (int i = 0; i < ORB_SLOTS; i++) {
			if (orbSlots[i] != null) {
				orbSlots[i].updateCooldown(deltaTime);
			}
		}
		
		for (int i = 0; i < PAPYRUS_SLOTS; i++) {
			if (papyrusSlots[i] != null) {
				papyrusSlots[i].updateCooldown(deltaTime);
			}
		}
		
	}

	public void addOrb(Orb newProjectile) {
		for (int i = 0; i < ORB_SLOTS; i++) {
			if (orbSlots[i] == null) {
				orbSlots[i] = newProjectile;
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
	
	public boolean tryToShoot(World world, Unit focus, Vector movement, Vector dir) {
		
		if (weaponSlot == null) {
			return false;
		}
		
		if (weaponSlot.getTimer().activate()) {
			world.add(new Projectile(focus, movement, dir, weaponSlot, getSelectedOrb()));
			return true;
		}
		
		return false;
		
	}

	public Armor getArmor() {
		return armorSlot;
	}

	public void setArmor(Armor newArmor) {
		armorSlot = newArmor;
	}

	public Orb getOrb(int index) {
		if (index < 0 || index >= ORB_SLOTS) {
			Main.error("index out of range, returning empty");
			return null;
		}
		return orbSlots[index];
	}

	public void setOrb(int slot, Orb newProjectile) {
		if (slot < 0 || slot >= ORB_SLOTS) {
			Main.error("slot out of range");
			return;
		}
		orbSlots[slot] = newProjectile;
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
	
	public Orb getSelectedOrb() {
		return getOrb(selectedOrb);
	}
	
	public void selectOrb(int i) {
		selectedOrb = i;
	}

	
	public void render(GameWindow window, Graphics g) {
		
		Vector topPos = new Vector(GameWindow.MARGIN, GameWindow.MARGIN);
		
		for (int i = 0; i < ORB_SLOTS; i++) {
			drawItemContainer(g, topPos, orbSlots[i], Orb.class, selectedOrb == i);
		}
		
		for (int i = 0; i < PAPYRUS_SLOTS; i++) {
			//will draw as selected/not selected based on cooldown
			drawItemContainer(g, topPos, papyrusSlots[i], Papyrus.class);
		}
		
		Vector bottomPos = new Vector(GameWindow.MARGIN, window.getDimensions().getY() - GameWindow.MARGIN - Item.getSlotWidth());
		
		drawItemContainer(g, bottomPos, weaponSlot, Weapon.class);
		drawItemContainer(g, bottomPos, armorSlot, Armor.class);
		
	}
	
	//might be nicer if we split this into three different methods
	//updates the vector
	private void drawItemContainer(Graphics g, Vector pos, Item item, Class<? extends Item> slotType, boolean flag) {
		
		int width;	
		if (item == null) {
			if (slotType.equals(Papyrus.class)) {
				width = Papyrus.drawEmptyPapyrus(g, pos, flag);
			} else if (slotType.equals(Orb.class)) {
				width = Orb.drawEmptyOrb(g, pos, flag);
			} else {
				width = Item.drawEmpty(g, pos);
			}
		} else {
			width = item.drawThumbnail(g, pos, flag);
		}
		
		pos.add(width + SLOT_SPACE, 0);
		
	}
	
	private void drawItemContainer(Graphics g, Vector pos, Item item, Class<? extends Item> slotType) {
		drawItemContainer(g, pos, item, slotType, !(item == null || !item.canUse()));
	}

}