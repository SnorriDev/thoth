package snorri.inventory;

public class Armor extends Item {

	private static final long serialVersionUID = 1L;

	public Armor(ItemType t) {
		super(t);
	}

	/**
	 * retrieve the block strength from the ItemType
	 */
	public int getBlock() {
		return (int) type.getProperty(0);
	}
	
	@Override
	public int getInvPos() {
		return 1;
	}
	
}
