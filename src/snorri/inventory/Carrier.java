package snorri.inventory;

public interface Carrier {

	public Inventory getInventory();
	
	default FullInventory getFullInventory() {
		return getInventory().getFullInventory();
	}
	
}
