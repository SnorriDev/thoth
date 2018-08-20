package snorri.inventory;

public interface DropContainer<D extends Droppable> {

	boolean add(D d);
	
	boolean remove(D d, boolean specific);

}
