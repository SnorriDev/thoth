package snorri.terrain;

import snorri.world.Vector;
import snorri.world.World;

public class SimpleTerrain extends TerrainGenerator {
	
	public SimpleTerrain(Vector dim) {
		super(dim);
	}
	
	public SimpleTerrain(int x, int y) {
		this(new Vector(x, y));
	}

	@Override
	public World genWorld() {
		World world = new World(genLevel());
		world.getLevel().computePathability();
		return world;
	}

}
