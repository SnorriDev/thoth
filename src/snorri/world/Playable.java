package snorri.world;

import snorri.entities.Entity;
import snorri.entities.Player;

public interface Playable extends Savable {
	
	World getCurrentWorld();
	
	public void update(Entity focus, double deltaTime);

	public Player computeFocus();
	
}
