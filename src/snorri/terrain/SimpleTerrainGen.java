package snorri.terrain;

import snorri.world.Vector;
import snorri.world.World;

public class SimpleTerrainGen extends TerrainGenerator {
	
	public SimpleTerrainGen(Vector dim) {
		super(dim);
	}
	
	public SimpleTerrainGen(int x, int y) {
		this(new Vector(x, y));
	}

	@Override
	public World genWorld() {
		World world = new World(genLevel());
		world.getLevel().computePathability();
		return world;
	}

}
