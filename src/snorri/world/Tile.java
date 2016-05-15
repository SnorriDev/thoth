package snorri.world;

import java.util.List;

import snorri.entities.Entity;

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
