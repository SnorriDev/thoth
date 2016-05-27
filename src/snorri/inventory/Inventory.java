package snorri.inventory;

public class Inventory { //TODO: implement cooldown
	private Item		weaponSlot;
	private Item		armorSlot;
	private final int	projectileSlotsSize	= 2;
	private Item[]		projectileSlots;
	private final int	papyrusSlotsSize	= 3;
	private Item[]		papyrusSlots;
						
	public Inventory() {
		weaponSlot = Item.newItem();
		armorSlot = Item.newItem();
		projectileSlots = new Item[projectileSlotsSize];
		for (int i = 0; i < projectileSlotsSize; i++ ) {
			projectileSlots[i] = Item.newItem();
		}
		papyrusSlots = new Item[papyrusSlotsSize];
		for (int i = 0; i < papyrusSlotsSize; i++ ) {
			papyrusSlots[i] = Item.newItem();
		}
		return;
	}
	
	public void addWeapon(Weapon newWeapon) {
		if (weaponSlot.getID() == 0) {
			weaponSlot = newWeapon;
			return;
		}
		else {
			System.out.println("error: weapon slot full, cannot add weapon");
			return;
		}
	}
	
	public void removeWeapon() {
		weaponSlot = Item.newItem();
	}
	
	public void addArmor(Armor newArmor) {
		if (armorSlot.getID() == 0) {
			armorSlot = newArmor;
			return;
		}
		else {
			System.out.println("error: armor slot full, cannot add armor");
			return;
		}
	}
	
	public void removeArmor() {
		armorSlot = Item.newItem();
	}
	
	public void addProjectile(Projectile newProjectile) {
		for (int i = 0; i < projectileSlotsSize; i++ ) {
			if (projectileSlots[i].getID() == 0) {
				projectileSlots[i] = newProjectile;
				return;
			}
		}
		System.out.println("error: all projectile slots full, cannot add projectile");
		return;
	}
	
	public void removeProjectile(int slot) {
		if (slot < 0 || slot >= projectileSlotsSize) {
			System.out.println("error: slot out of range");
			return;
		}
		projectileSlots[slot] = Item.newItem();
		return;
	}
	
	public void addPapyrus(Papyrus newPapyrus) {
		for (int i = 0; i < papyrusSlotsSize; i++ ) {
			if (papyrusSlots[i].getID() == 0) {
				papyrusSlots[i] = newPapyrus;
				return;
			}
		}
		System.out.println("error: all papyrus slots full, cannot add papyrus");
		return;
	}
	
	public void removePapyrus(int slot) {
		if (slot < 0 || slot >= papyrusSlotsSize) {
			System.out.println("error: slot out of range");
			return;
		}
		papyrusSlots[slot] = Item.newItem();
		return;
	}
	
	public void addSpell(Papyrus newPapyrus) {
		addPapyrus(newPapyrus);
		return;
	}
	
	public void removeSpell(int slot) {
		removePapyrus(slot);
		return;
	}
	
	public Item getWeapon() {
		return weaponSlot;
	}
	
	public void setWeapon(Weapon newWeapon) {
		weaponSlot = newWeapon;
		return;
	}
	
	public void setWeapon(EmptyInvSlot nothing) {
		weaponSlot = nothing;
		return;
	}
	
	public void setWeapon() {
		weaponSlot = Item.newItem();
		return;
	}
	
	public Item getArmor() {
		return armorSlot;
	}
	
	public void setArmor(Weapon newArmor) {
		armorSlot = newArmor;
		return;
	}
	
	public void setArmor(EmptyInvSlot nothing) {
		armorSlot = nothing;
		return;
	}
	
	public void setArmor() {
		armorSlot = Item.newItem();
		return;
	}
	
	public Item getProjectile(int index) {
		if (index < 0 || index >= projectileSlotsSize) {
			System.out.println("error: index out of range, returning EmptyInvSlot");
			return Item.newItem();
		}
		return projectileSlots[index];
	}
	
	public void setProjectile(int slot, Projectile newProjectile) {
		if (slot < 0 || slot >= projectileSlotsSize) {
			System.out.println("error: slot out of range");
			return;
		}
		projectileSlots[slot] = newProjectile;
		return;
	}
	
	public void setProjectile(int slot, EmptyInvSlot nothing) {
		if (slot < 0 || slot >= projectileSlotsSize) {
			System.out.println("error: slot out of range");
			return;
		}
		projectileSlots[slot] = nothing;
		return;
	}
	
	public void setProjectile(int slot) {
		if (slot < 0 || slot >= projectileSlotsSize) {
			System.out.println("error: slot out of range");
			return;
		}
		projectileSlots[slot] = Item.newItem();
		return;
	}
	
	public Item getPapyrus(int index) {
		if (index < 0 || index >= papyrusSlotsSize) {
			System.out.println("error: index out of range, returning EmptyInvSlot");
			return Item.newItem();
		}
		return papyrusSlots[index];
	}
	
	public void setPapyrus(int slot, Papyrus newPapyrus) {
		if (slot < 0 || slot >= papyrusSlotsSize) {
			System.out.println("error: slot out of range");
			return;
		}
		papyrusSlots[slot] = newPapyrus;
		return;
	}
	
	public void setPapyrus(int slot, EmptyInvSlot nothing) {
		if (slot < 0 || slot >= papyrusSlotsSize) {
			System.out.println("error: slot out of range");
			return;
		}
		papyrusSlots[slot] = nothing;
		return;
	}
	
	public void setPapyrus(int slot) {
		if (slot < 0 || slot >= papyrusSlotsSize) {
			System.out.println("error: slot out of range");
			return;
		}
		papyrusSlots[slot] = Item.newItem();
		return;
	}
	
	public Item getSpell(int index) {
		return getSpell(index);
	}
	
	public void setSpell(int slot, Papyrus newPapyrus) {
		setPapyrus(slot, newPapyrus);
		return;
	}
	
	public void setSpell(int slot, EmptyInvSlot nothing) {
		setPapyrus(slot, nothing);
		return;
	}
	
	public void setSpell(int slot) {
		setPapyrus(slot);
		return;
	}
}