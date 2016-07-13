package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Campaign;
import snorri.world.Campaign.WorldId;
import snorri.world.Playable;
import snorri.world.Vector;

public class Portal extends Detector {
	
	private static final long serialVersionUID = 1L;
	private final WorldId world;
	private final Vector dest;
	
	public Portal(Vector pos, WorldId world, Vector dest) {
		super(pos, 30);
		this.world = world;
		this.dest = dest;
		treeMember = true;
		age = -1;
		ignoreCollisions = true;
	}
	
	public Portal(Vector pos, String world, Vector dest) {
		this(pos, WorldId.fromString(world), dest);
	}
	
	@Override
	public void onCollision(CollisionEvent e) {
		Playable universe = ((GameWindow) Main.getWindow()).getUniverse();
		if (universe instanceof Campaign) { //TODO multiworld?
			if ((!(e.getTarget() instanceof Detector) || ((Detector) e.getTarget()).isTreeMember())) {
				((Campaign) universe).teleport(e.getTarget(), world, dest);
			}
		} else {
			Main.error("can't use portal outside campaign");
		}
	}

}
