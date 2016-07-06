package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.inventory.Droppable;
import snorri.main.Main;
import snorri.world.Vector;

public class Drop extends Detector {
	
	private static final long serialVersionUID = 1L;
	private final Droppable prize;
	
	public Drop(Vector pos, Droppable prize) {
		super(pos, 30);
		this.prize = prize;
		treeMember = true;
		age = -1;
		ignoreCollisions = true;
	}
	
	public Droppable getPrize() {
		return prize;
	}
	
	@Override
	public void onCollision(CollisionEvent e) {
		if (e.getTarget() instanceof Player) {
			Main.log("you acquired " + getPrize());
			((Player) e.getTarget()).getFullInventory().add(getPrize());
			e.getWorld().delete(this);
		}
	}

}
