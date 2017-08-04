package snorri.world;

import snorri.entities.Entity;
import snorri.entities.Player;

/**
 * This interface is used for game objects that can be played by the player.
 * @author snorri
 *
 */

public interface Playable extends Savable {
	
	World getCurrentWorld();
	
	public void update(Entity focus, double deltaTime);

	public Player computeFocus();
	
}
