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
	
	// TODO(lambdaviking): Save this stuff.
	static Layer fromYAML(World world, Map<String, Object> params) throws IOException {
		String type = (String) params.get("type");
		switch (type) {
		case "background":
			return BackgroundLayer.fromYAML(world, params);
		case "tile":
			return TileLayer.fromYAML(world, params);
		case "entity":
			return EntityLayer.fromYAML(world, params);
		default:
			throw new IllegalArgumentException("Unknown Layer type " + type + ".");
		}
	}
	
}
