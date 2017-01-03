package snorri.inventory;

import java.awt.Graphics;
import java.io.Serializable;

import snorri.audio.Audio;
import snorri.entities.Player;
import snorri.entities.Projectile;
import snorri.entities.Unit;
import snorri.keyboard.Key;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.parser.Grammar;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.World;

public class Inventory implements Serializable {
	
	/**
	 * Holds the inventory of a player
	 */
	private static final long serialVersionUID = 1L;

	private Unit player;
	private FullInventory fullInventory;
	
	private Weapon weaponSlot;
	private Armor armorSlot;
	private Orb[] orbSlots;
	private Papyrus[] papyrusSlots;
	private int selectedOrb = 0;
	
	public static final Key[] ORB_KEYS = new Key[] {Key.FOUR, Key.FIVE};
	public static final Key[] PAPYRUS_KEYS = new Key[] {Key.ONE, Key.TWO, Key.THREE};

	private static final int ORB_SLOTS = 2;
	private static final int PAPYRUS_SLOTS = 3;
	
	public Inventory(Unit player) {
		this.player = player;
		fullInventory = new FullInventory(this);
		orbSlots = new Orb[ORB_SLOTS];
		papyrusSlots = new Papyrus[PAPYRUS_SLOTS];
	}
	
	public void update(double deltaTime) {

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

	public FullInventory getFullInventory() {
		return fullInventory;
	}

	public boolean addOrb(Orb newProjectile) {
		for (int i = 0; i < ORB_SLOTS; i++) {
			if (orbSlots[i] == null) {
				orbSlots[i] = newProjectile;
				return true;
			}
		}
		return false;
	}

	public boolean addPapyrus(Papyrus newPapyrus) {
		for (int i = 0; i < PAPYRUS_SLOTS; i++) {
			if (papyrusSlots[i] == null) {
				papyrusSlots[i] = newPapyrus;
				return true;
			}
		}
		return false;
	}
	
	public boolean addWeapon(Weapon newWeapon) {
		if (weaponSlot == null) {
			weaponSlot = newWeapon;
			return true;
		}
		return false;
	}
	
	public boolean addArmor(Armor newArmor) {
		if (armorSlot == null) {
			armorSlot = newArmor;
			return true;
		}
		return false;
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
		
		if (weaponSlot == null ||  dir == null || dir.equals(Vector.ZERO) || dir.notInPlane()) {
			return false;
		}
		
		if (weaponSlot.getTimer().activate()) {
			Audio.playClip(weaponSlot.getClip());
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
		int oldI = getIndex(newProjectile);
		if (oldI != Integer.MAX_VALUE) {
			removeOrb(oldI);
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
		int oldI = getIndex(newPapyrus);
		if (oldI != Integer.MAX_VALUE) {
			papyrusSlots[oldI] = null;
		}
		papyrusSlots[slot] = newPapyrus;
	}
	
	public Orb getSelectedOrb() {
		return getOrb(selectedOrb);
	}
	
	public void selectOrb(int i) {
		if (getOrb(i) != null) {
			selectedOrb = i;
		}
	}
	
	public void removeOrb(int i) {
		orbSlots[i] = null;
		if (selectedOrb == i) {
			selectedOrb = 0;
		}
	}

	
	public void render(GameWindow window, Graphics g) {
		
		//4,5
		for (int i = 0; i < ORB_SLOTS; i++) {
			drawItemContainer(g, i + PAPYRUS_SLOTS, true, orbSlots[i], Orb.class, selectedOrb == i);
		}
		
		//1,2,3
		for (int i = 0; i < PAPYRUS_SLOTS; i++) {
			drawItemContainer(g, i, true, papyrusSlots[i], Papyrus.class);
		}
		
		//Sling
		drawItemContainer(g, 0, false, weaponSlot, Weapon.class);
		//drawItemContainer(g, 1, false, armorSlot, Armor.class);
		
	}
	
	private void drawItemContainer(Graphics g, int i, boolean top, Item item, Class<? extends Item> slotType) {
		drawItemContainer(g, i, top, item, slotType, item != null && item.canUse());
	}
	
	private void drawItemContainer(Graphics g, int i, boolean top, Item item, Class<? extends Item> slotType, boolean flag) {
		
		if (item == null) {
			Item.drawEmpty(g, i, top);
		} else {
			item.drawThumbnail(g, i, top, flag);
		}
		
	}
	
	private boolean compare(Droppable d1, Droppable d2, boolean specific) {
		return (specific && d1 == d2) || (!specific && d1.equals(d2));
	}
	
	public boolean add(Droppable d) {
		
		if (d == null) {
			return false;
		}
		
		if (d instanceof Papyrus) {
			addPapyrus((Papyrus) d);
		}
		if (d instanceof Orb) {
			addOrb((Orb) d);
		}
		if (d instanceof Weapon) {
			addWeapon((Weapon) d);
		}
		if (d instanceof Armor) {
			addArmor((Armor) d);
		}
		
		TriggerType.ACQUIRE.activate(d.toString());
		
		if (d instanceof Item && ((Item) d).getSpell() != null) {
			for (String word : Grammar.getWords(((Item) d).getSpell().getOrthography())) {
				add(new VocabDrop(word));
			}
		}
		
		if (Main.getWindow() instanceof GameWindow && player instanceof Player) {
			((GameWindow) Main.getWindow()).showMessage(d);
		}
		return fullInventory.add(d);

	}
	
	public boolean remove(Droppable d, boolean specific) {
				
		if (d instanceof Item) {
			
			if (compare(d, weaponSlot, specific)) {
				weaponSlot = null;
			}
			if (compare(d, armorSlot, specific)) {
				armorSlot = null;
			}
			for (int i = 0; i < orbSlots.length; i++) {
				if (compare(d, orbSlots[i], specific)) {
					orbSlots[i] = null;
				}
			}
			for (int i = 0; i < papyrusSlots.length; i++) {
				if (compare(d, papyrusSlots[i], specific)) {
					papyrusSlots[i] = null;
				}
			}
		
		}
		
		return fullInventory.remove(d);
		
	}
	
	public int getIndex(Orb orb) {
		for (int i = 0; i < orbSlots.length; i++) {
			if (orb == orbSlots[i]) {
				return i;
			}
		}
		return Integer.MAX_VALUE;
	}
	
	private int getIndex(Papyrus papyrus) {
		for (int i = 0; i < papyrusSlots.length; i++) {
			if (papyrus == papyrusSlots[i]) {
				return i;
			}
		}
		return Integer.MAX_VALUE;
	}
	
	private int getIndex(Weapon weapon) {
		return weaponSlot == weapon ? 0 : Integer.MAX_VALUE;
	}
	
	private int getIndex(Armor armor) {
		return armorSlot == armor ? 0 : Integer.MAX_VALUE;
	}
	
	/**
	 * @return <code>Integer.MAX_VALUE</code> iff the item does not appear
	 */
	public int getIndex(Item item) {
		if (item instanceof Weapon) {
			return getIndex((Weapon) item);
		}
		if (item instanceof Armor) {
			return getIndex((Armor) item);
		}
		if (item instanceof Orb) {
			return getIndex((Orb) item);
		}
		if (item instanceof Papyrus) {
			return getIndex((Papyrus) item);
		}
		return Integer.MAX_VALUE;
	}
	
	/**
	 * the character to show in parentheses after
	 * the item in the inventory HUD
	 */
	public Key getKey(Item item) {
		
		int index;
		if (item instanceof Orb) {
			index = getIndex((Orb) item);
			if (index == Integer.MAX_VALUE) {
				return null;
			}
			return ORB_KEYS[getIndex((Orb) item)];
		}
		if (item instanceof Papyrus) {
			index = getIndex((Papyrus) item);
			if (index == Integer.MAX_VALUE) {
				return null;
			}
			return PAPYRUS_KEYS[index];
		}
		
		return null;
	}
	
	/**
	 * Check if inventory buttons are pressed in the <code>keyStates</code> map of the current focused window
	 */
	public void checkKeys() {
		
		for (int i = 0; i < ORB_KEYS.length; i++) {
			if (ORB_KEYS[i].isPressed()) {
				selectOrb(i);
			}
		}
		
		for (int i = 0; i < PAPYRUS_KEYS.length; i++) {
			if (PAPYRUS_KEYS[i].isPressed()) {
				usePapyrus(i);
			}
		}
		
	}

}