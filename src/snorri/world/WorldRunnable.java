package snorri.world;

//TODO unify this with lambdas, etc.

@FunctionalInterface
public interface WorldRunnable {

	public void exec(World world);
	
}
