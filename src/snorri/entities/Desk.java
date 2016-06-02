package snorri.entities;

import snorri.world.Vector;

public class Desk extends Entity {

	/**
	 * Desks are the stations where you can edit inventory and spells
	 * Press space to interact with them
	 */
	private static final long serialVersionUID = 1L;
	public static final int INTERACT_RANGE = 5;
	
	public Desk(Vector pos) {
		super(pos, 5);
	}

}
