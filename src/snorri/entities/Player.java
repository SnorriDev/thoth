package snorri.entities;

import snorri.inventory.Inventory;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.inventory.Weapon;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Player extends Unit {

	private Inventory inventory;
	
	public Player(Vector pos) {
		
		super(pos);
		inventory = new Inventory();
		
		inventory.setWeapon((Weapon) Item.newItem(ItemType.SLING));
		Main.log("Initial weapon: " + inventory.getWeapon().getType());
		
	}
	
	@Override
	public Object get(World world, AbstractSemantics attr) {

		if (attr == AbstractSemantics.WEAPON) {
			return inventory.getWeapon();
		}
		
		return super.get(world, attr);
		
	}
	
	public Inventory getInventory() {
		return inventory;
	}

}
