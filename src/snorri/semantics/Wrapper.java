package snorri.semantics;

import snorri.semantics.nouns.Nominal;

public class Wrapper<T> implements Nominal {

	private static final long serialVersionUID = 1L;

	protected final T value;
	
	public Wrapper(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
}
