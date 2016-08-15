package snorri.overlay;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

public class SortedListModel<E> extends AbstractListModel<E> {

	private static final long serialVersionUID = 1L;
	
	SortedSet<E> model;

	public SortedListModel() {
		model = new TreeSet<>();
	}

	public int getSize() {
		return model.size();
	}

	@SuppressWarnings("unchecked")
	public E getElementAt(int index) {
		return (E) (model.toArray()[index]);
	}

	public void addElement(E element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}
	
	public void redraw() {
		fireContentsChanged(this, 0, getSize());
	}

	public void addAll(E[] elements) {
		Collection<E> c = Arrays.asList(elements);
		model.addAll(c);
		fireContentsChanged(this, 0, getSize());
	}

	public void clear() {
		model.clear();
		fireContentsChanged(this, 0, getSize());
	}

	public boolean contains(Object element) {
		return model.contains(element);
	}

	public Object firstElement() {
		return model.first();
	}

	public Iterator<E> iterator() {
		return model.iterator();
	}

	public Object lastElement() {
		return model.last();
	}

	public boolean removeElement(Object element) {
		boolean removed = model.remove(element);
		if (removed) {
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}
}