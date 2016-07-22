package snorri.terrain;

import java.util.ArrayList;
import java.util.List;

import snorri.world.Level;
import snorri.world.Vector;

/**
 * Void tiles in a structure do not get placed.
 * That's how we can accomplish stacking, etc.
 */

public class Structure {

	private final Level level;
	private final List<Vector> doors;
	
	public Structure(Level level, List<Vector> doors) {
		this.level = level;
		this.doors = doors;
	}
	
	public Structure(Level level) {
		this(level, new ArrayList<>());
	}
	
	public Level getLevel() {
		return level;
	}
	
	public Vector getClosestDoor(Vector pos) {
		Vector res = null;
		double minDistance = Double.MAX_VALUE;
		for (Vector door : doors) {
			if (door.distance(pos) < minDistance) {
				res = door;
			}
		}
		return res;
	}
	
	public List<Vector> getExits(Vector entrance) {
		List<Vector> exits = new ArrayList<>(doors);
		exits.remove(entrance);
		return exits;
	}
	
}
