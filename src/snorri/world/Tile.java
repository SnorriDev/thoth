package snorri.world;

import java.util.List;

public class Tile {

	private List<Entity> entities;
	private int type;
	
	public Tile(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public boolean isPassable() {
		return true;
	}
	
}
