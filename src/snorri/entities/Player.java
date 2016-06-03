package snorri.entities;

import snorri.inventory.Inventory;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.inventory.Orb;
import snorri.inventory.Papyrus;
import snorri.inventory.Weapon;
import snorri.parser.Grammar;
import snorri.world.Vector;
import snorri.world.World;

public class Player extends Unit {

	private static final long serialVersionUID = 1L;
	private Inventory inventory;
	
	public Player(Vector pos) {
		
		super(pos);
		inventory = new Inventory(this);
		
		//test inventory
		inventory.setWeapon((Weapon) Item.newItem(ItemType.SLING));
		
		inventory.addPapyrus((Papyrus) Item.newItem(ItemType.PAPYRUS));
		inventory.getPapyrus(0).setSpell(Grammar.parseString("bm m=f"));
		
		inventory.addProjectile((Orb) Item.newItem(ItemType.PELLET));
		inventory.getProjectile(0).setSpell(Grammar.parseString("bm m=k"));
		
	}
	
	@Override
	public void update(World world, float deltaTime) {
		super.update(world, deltaTime);
		inventory.update(deltaTime);
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
