package snorri.terrain;

import snorri.world.Vector;
import snorri.world.World;

public class GraphGen implements Generator {

	private static final Vector ROOM_DIM = new Vector(13, 8);
	private static final int DIM_RANGE = 2;
	
	public GraphGen() {
		RoomGen roomGen = new RoomGen(ROOM_DIM, DIM_RANGE);
	}
	
	@Override
	public World genWorld() {
		return null;
	}

}
