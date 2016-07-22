package snorri.world;

import snorri.entities.Player;

public interface Playable extends Savable {
	
	World getCurrentWorld();
	
	public void update(double deltaTime);

	public Player computeFocus();
	
}
