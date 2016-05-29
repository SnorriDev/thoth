package snorri.world;

import snorri.semantics.Nominal;

public enum Tile implements Nominal {

	SAND(true),
	WALL(false),
	TREE(false),
	LILY(true);
	
	//TODO: pass an array of textures for each one
	
	public static final int WIDTH = 32;
		
	private boolean pathable;
	
	Tile(boolean pathable) {
		this.pathable = pathable;
	}
	
	public static Tile byId(int id) {
		return values()[id];
	}
	
	public int getId() {
		return this.ordinal();
	}
	
	public boolean isPathable() {
		return pathable;
	}
	
}
