package snorri.world;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Interface for layers that can be added to a Level.
 * @author tojaroslaw, lambdaviking
 *
 */
public interface Layer extends Renderable {

	public enum LayerType {
		
		BACKGROUND(BackgroundLayer::fromYAML),
		TILE(TileLayer::wrappedFromYAML),
		ENTITY(EntityLayer::wrappedFromYAML);
		
		private BiFunction<World, Map<String, Object>, Layer> fromYAML;
		
		LayerType(BiFunction<World, Map<String, Object>, Layer> fromYAML) {
			this.fromYAML = fromYAML;
		}
		
		public Layer fromYAML(World world, Map<String, Object> params) {
			return fromYAML.apply(world, params);
		}
		
	}
	
	public boolean canShootOver(Vector position);	
	
	static Layer fromYAML(World world, Map<String, Object> params) {
		String type = (String) params.get("type");
		return LayerType.valueOf(type).fromYAML(world, params);
	}
	
}
