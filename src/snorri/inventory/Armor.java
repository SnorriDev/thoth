package snorri.inventory;



public class Armor extends Item {

	public Armor(ItemType t) {
		super(t);
	}

	//retrieve the block strength from the ItemType
	public int getBlock() {
		return (int) type.getProperty(0);
	}
	
}
