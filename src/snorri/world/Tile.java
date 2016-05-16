package snorri.world;

public class Tile {

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
