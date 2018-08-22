package snorri.world;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for layers that can be added to a Level.
 * @author tojaroslaw, lambdaviking
 *
 */
public interface Layer extends Renderable {

	public boolean canShootOver(Vector position);	
	
	static Layer fromYAML(World world, String type, Map<String, Object> params) throws IOException {
		switch (type) {
		case "background":
			return BackgroundLayer.fromYAML(params);
		case "tile":
			return Level.fromYAML(params);
		case "entity":
			return EntityLayer.fromYAML(world, params);
		default:
			throw new IllegalArgumentException("Unknown Layer type " + type + ".");
		}
	}
	
}
