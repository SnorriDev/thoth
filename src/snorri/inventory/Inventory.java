package snorri.inventory;

import snorri.main.Main;

public class Inventory { // TODO: implement cooldown
	private Weapon weaponSlot;
	private Armor armorSlot;
	private Projectile[] projectileSlots;
	private Papyrus[] papyrusSlots;

	private static final int PROJECTILE_SLOTS = 2;
	private static final int PAPYRUS_SLOTS = 3;

	public Inventory() {
		projectileSlots = new Projectile[PROJECTILE_SLOTS];
		papyrusSlots = new Papyrus[PAPYRUS_SLOTS];
	}

	public void addProjectile(Projectile newProjectile) {
		for (int i = 0; i < PROJECTILE_SLOTS; i++) {
			if (projectileSlots[i].isEmpty()) {
				projectileSlots[i] = newProjectile;
				return;
			}
		}
		Main.error("all projectile slots full, cannot add projectile");
	}

	public void addPapyrus(Papyrus newPapyrus) {
		for (int i = 0; i < PAPYRUS_SLOTS; i++) {
			if (papyrusSlots[i].isEmpty()) {
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

}