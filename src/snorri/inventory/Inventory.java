package snorri.inventory;

import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import snorri.entities.Unit;
import snorri.events.CastEvent.Caster;
import snorri.inventory.Item.ItemType;
import snorri.windows.GameWindow;
import snorri.world.Vector;
import snorri.world.World;

/**
 * Holds the inventory of a unit.
 * @author snorri
 * 
 */
public class Inventory implements Serializable, DropContainer<Droppable> {
	
	private static final long serialVersionUID = 1L;
		
	private Unit player;
	
	private Weapon weaponSlot;
	private Orb orbSlot;
	private Papyrus papyrusSlot;
		
	public Inventory(Unit player) {
		this.player = player;
		papyrusSlot = (Papyrus) Item.newItem(ItemType.PAPYRUS);
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
	
	public Orb getOrb() {
		return orbSlot;
	}
	
	public void render(GameWindow window, Graphics g) {
		drawItemContainer(g, 0, false, weaponSlot, Weapon.class);
//		TODO: Add back multiple papyrusSlots.
		drawItemContainer(g, 1, false, papyrusSlot, Papyrus.class);
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
		if (d instanceof PapyrusDrop) {
			papyrusSlot.addPapyri(((PapyrusDrop) d).getQuantity());
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
		
		if (d instanceof PapyrusDrop) {
			return papyrusSlot.removePapyri(((PapyrusDrop) d).getQuantity());
		}
		
		return false;	
	}
	
	public void attack(World world, Vector momentum, Vector dir) {
		if (weaponSlot == null || orbSlot == null || dir == null || dir.equals(Vector.ZERO) || dir.notInPlane()) {
			return;
		}
		weaponSlot.attackIfPossible(world, player, momentum, dir, orbSlot);
	}
	
	public void cast(World world, Vector castPos) {
		if (castPos != null && player instanceof Caster) {
			papyrusSlot.queueSpell(world, (Caster) player, castPos);
		}
	}

	public ListModel<Item> getItemModel() {
		DefaultListModel<Item> model = new DefaultListModel<>();
		model.addElement(weaponSlot);
		model.addElement(papyrusSlot);
		return model;
	}
	
}