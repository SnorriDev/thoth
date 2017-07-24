package snorri.world;

//TODO unify this with lambdas, etc.

@FunctionalInterface
public interface Executable<E> {

	public void exec(E obj);
	
}
