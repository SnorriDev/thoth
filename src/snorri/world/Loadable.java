package snorri.world;

import java.io.File;
import java.io.IOException;

/**
 * Interface describing loadable objects.
 * @author snorri
 * 
 * This class corresponds with Savable.
 * 
 * When creating new object hierarchies, you should use a static factory pattern (see EntityLayer, for example) instead of this interface.
 *
 */
public interface Loadable {

	public void load(File folder) throws IOException;
	
}
