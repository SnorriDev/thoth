package snorri.world;

public class Level {

	private Tile[][] map;
	private Vector dim;
	
	//TODO: load from file, not empty grid with parameters
	//not that indexing conventions are Cartesian, not matrix-based
	
	public Level(int width, int height) {
		map = new Tile[width][height];
		dim = new Vector(width, height);
	}
	
	public void setTile(int x, int y, Tile t) {
		map[x / Tile.WIDTH][y / Tile.WIDTH] = t;
	}
	
	public Tile getTile(int x, int y) {
		return map[x / Tile.WIDTH][y / Tile.WIDTH];
	}
	
	public Tile getTile(Vector v) {
		return getTile(v.getX(), v.getY());
	}
	
	public Vector getDimensions() {
		return dim;
	}
	
}
