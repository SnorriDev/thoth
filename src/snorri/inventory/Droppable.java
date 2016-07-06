package snorri.inventory;

import java.io.Serializable;

public interface Droppable extends Serializable {
		
	public int getMaxQuantity();
	
	//TODO use this method to update charges on consumables, etc.
	public boolean stack(Droppable other);
	
}
