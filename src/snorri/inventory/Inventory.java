package snorri.inventory;

import java.awt.Graphics;
import java.io.Serializable;

import snorri.entities.Unit;
import snorri.events.SpellEvent.Caster;
import snorri.inventory.Item.ItemType;
import snorri.keyboard.Key;
import snorri.main.FocusedWindow;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Inventory implements Serializable, Container<Droppable> {
	
	/** Holds the inventory of a player */
	private static final long serialVersionUID = 1L;
	
	Key PAPYRUS_KEY = Key.Q;
	
	private Unit player;
	
	private Weapon weaponSlot;
	private Orb orbSlot;
	private Papyrus papyrusSlot; // can enchant this papyrus to hardcode AI spell behavior
	private int numPapyri;
		
	public Inventory(Unit player) {
		this.player = player;
		papyrusSlot = (Papyrus) Item.newItem(ItemType.PAPYRUS);
		numPapyri = 0;
	}
	
	public void update(double deltaTime) {
		
		if (weaponSlot != null) {
			weaponSlot.updateCooldown(deltaTime);
		}
		
		if (orbSlot != null) {
			orbSlot.updateCooldown(deltaTime);
		}
		
		papyrusSlot.updateCooldown(deltaTime);
		
	}
	
	public void setOrb(Orb newOrb) {
		orbSlot = newOrb;
	}
	
	public void addPapyrus() {
		numPapyri++;
	}
	
	public boolean addWeapon(Weapon newWeapon) {
		if (weaponSlot == null) {
			weaponSlot = newWeapon;
			return true;
		}
		return false;
	}
	
	public boolean addOrb(Orb newOrb) {
		if (orbSlot == null) {
			orbSlot = newOrb;
			return true;
		}
		return false;
	}
	
	public Weapon getWeapon() {
		return weaponSlot;
	}
	
	public void setWeapon(Weapon newWeapon) {
		weaponSlot = newWeapon;
	}
	
	/**
	 * For a player this papyrus is blank, but for a boss, it will have a spell enchanted on it.
	 * @return the papyrus in this inventory
	 */
	public Papyrus getPapyrus() {
		return papyrusSlot;
	}
	
	public int getNumPapyri() {
		return numPapyri;
	}
	
	public Orb getOrb() {
		return orbSlot;
	}
	
	public void render(GameWindow window, Graphics g) {
		
		// Papyrus Slot
		drawItemContainer(g, 0, true, papyrusSlot, Papyrus.class);
		
		// Sling and Orb
		drawItemContainer(g, 0, false, weaponSlot, Weapon.class);
		drawItemContainer(g, 1, false, orbSlot, Orb.class);
		
	}
	
	private void drawItemContainer(Graphics g, int i, boolean top, Item item, Class<? extends Item> slotType) {
		drawItemContainer(g, i, top, item, slotType, item != null && item.canUse());
	}
	
	private void drawItemContainer(Graphics g, int i, boolean top, Item item, Class<? extends Item> slotType, boolean flag) {
		
		if (item == null) {
			Item.drawEmpty(g, i, top);
		}
		else {
			item.drawThumbnail(g, i, top, flag);
		}
		
	}
	
	private boolean compare(Droppable d1, Droppable d2, boolean specific) {
		return (specific && d1 == d2) || (!specific && d1.equals(d2));
	}
	
	@Override
	public boolean add(Droppable d) {
		
		if (d instanceof Orb) {
			addOrb((Orb) d);
			return true;
		}
		if (d instanceof Weapon) {
			addWeapon((Weapon) d);
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public boolean remove(Droppable d, boolean specific) {
		
		if (d instanceof Item) {
			
			if (compare(d, weaponSlot, specific)) {
				weaponSlot = null;
				return true;
			}
			if (compare(d, orbSlot, specific)) {
				orbSlot = null;
				return true;
			}
			
		}
		
		return false;
				
	}
	
	private int getIndex(Weapon weapon) {
		return weaponSlot == weapon ? 0 : Integer.MAX_VALUE;
	}
	
	private int getIndex(Orb orb) {
		return orbSlot == orb ? 0 : Integer.MAX_VALUE;
	}
	
	/** @return <code>Integer.MAX_VALUE</code> iff the item does not appear */
	public int getIndex(Item item) {
		if (item instanceof Weapon) {
			return getIndex((Weapon) item);
		}
		if (item instanceof Orb) {
			return getIndex((Orb) item);
		}
		return Integer.MAX_VALUE;
	}
	
	public void checkKeys() {
		
		FocusedWindow<?> window = (FocusedWindow<?>) Main.getWindow();
		Vector movement = window.getMovementVector();
		Vector dir = window.getShotDirection();
		World world = window.getWorld();
			
		if (PAPYRUS_KEY.isPressed() && player instanceof Caster) {
			papyrusSlot.cast(world, (Caster) player);
		}
		
		attack(world, movement, dir);
				
	}
	
	public void attack(World world, Vector movement, Vector dir) {
		if (weaponSlot == null || orbSlot == null || dir == null || dir.equals(Vector.ZERO) || dir.notInPlane()) {
			return;
		}
		weaponSlot.attack(world, player, movement, dir, orbSlot);
	}
	
}