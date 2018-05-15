package snorri.inventory;

public interface Container<D> {

	boolean add(D d);
	
	boolean remove(D d, boolean specific);

}
