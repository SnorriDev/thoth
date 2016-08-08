package snorri.inventory;

public interface Carrier {

	public Inventory getInventory();
	
	@Deprecated
	default FullInventory getFullInventory() {
		return getInventory().getFullInventory();
	}
	
}
