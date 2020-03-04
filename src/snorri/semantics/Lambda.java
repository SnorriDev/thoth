package snorri.semantics;

public interface Lambda<A, B> {
	
	public abstract B apply(A other);

}
