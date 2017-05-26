package snorri.events;

import snorri.entities.Player;
import snorri.world.World;

public class InteractEvent {

	private final World world;
	private final Player source;
	
	public InteractEvent(World world, Player source) {
		this.world = world;
		this.source = source;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Player getPlayer() {
		return source;
	}
	
}
