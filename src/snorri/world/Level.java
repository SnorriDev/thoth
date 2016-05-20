package snorri.world;

public class Level {

	private Tile[][] map;
	//store entities in each tile
	
	//TODO: load from file, not empty grid with parameters
	
	public Level(int height, int width) {
		map = new Tile[width][height];
	}
	
	public void setTile(int x, int y, Tile t) {
		map[x][y] = t;
	}
	
	public Tile getTile(int x, int y) {
		return map[x][y];
	}
	
}
