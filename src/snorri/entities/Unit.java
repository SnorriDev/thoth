package snorri.entities;

import snorri.world.Vector;

public class Unit extends Entity {

	private static final int BASE_SPEED = 2;
	
	public Unit(Vector pos) {
		super(pos, 3);
		// TODO Auto-generated constructor stub
	}

	public void walk(Vector direction, EntityGroup col) {
		Vector dir = direction.copy();
		
		if (dir.equals(Vector.ZERO)) {
			return;
		}
		
		dir.multiply(getSpeed());
		col.move(this, dir);
	}
	
	//override this for faster entities
	protected int getSpeed() {
		return BASE_SPEED;
	}
	
}
