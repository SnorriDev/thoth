package snorri.entities;

import snorri.world.Vector;

public class Mummy extends Enemy {

	private static final long serialVersionUID = 1L;
	
	public Mummy(Vector pos) {
		super(pos);
	}
	
	public Mummy(Vector pos, Entity target) {
		super(pos, target);
	}

}
