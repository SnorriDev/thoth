package snorri.entities;

import snorri.world.Vector;

/**
 * Spawn point marker used when a player enters a new level.
 * @author snorri
 *
 */

public class Spawn extends Entity {

	private static final long serialVersionUID = 1L;
	
	public Spawn(Vector pos) {
		super(pos);
	}
	
}
