package snorri.world;

public enum Tile {

	SAND(0, true),
	WALL(1, false);
	
	public static final int WIDTH = 32;
		
	private int id;
	private boolean pathable;
	
	Tile(int id, boolean pathable) {
		this.id = id;
		this.pathable = pathable;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isPathable() {
		return pathable;
	}
	
}
