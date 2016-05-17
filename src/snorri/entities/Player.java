package snorri.entities;

import snorri.world.Position;

public class Player extends Entity {

	public Player(Position pos) {
		super(pos);
	}
	
	@Override
	public int getSpeed() {
		return 2 * super.getSpeed();
	}

}
