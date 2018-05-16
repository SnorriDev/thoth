package snorri.inventory;

public interface Carrier extends DropContainer<Droppable> {

	public Inventory getInventory();
	
	default boolean add(Droppable d) {
		return getInventory().add(d);
	}
	
	default boolean remove(Droppable d, boolean specific) {
		return getInventory().remove(d, specific);
	}
	
}
