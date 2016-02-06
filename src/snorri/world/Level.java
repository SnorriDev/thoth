package snorri.world;

public class Level {

	private Tile[][] map;
	//store entities in each tile
	
	//TODO: load from file, not empty grid with parameters
	
	public Level(int height, int width) {
		map = new Tile[width][height];
	}
	
	public Tile getTile(int x, int y) {
		return map[x][y];
	}
	
}
