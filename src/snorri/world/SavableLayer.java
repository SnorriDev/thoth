package snorri.world;

/**
 * Interface for layers that are Savable.
 * @author lambdaviking
 * 
 */
public interface SavableLayer extends Layer, Savable {
	
	/** Get the filename for saving the Level in the World folder. */
	public String getFilename();

}
