package snorri.entities;

import snorri.events.CollisionEvent;
import snorri.inventory.Droppable;
import snorri.inventory.RandomDrop;
import snorri.world.Vector;

public class Drop extends Detector {
	
	private static final long serialVersionUID = 1L;
	
	private final Droppable prize;

	
	public Drop(Vector pos, Droppable prize) {
		super(pos, 15);
		this.prize = prize;
		age = -1;
		ignoreCollisions = true;
		animation = prize.getAnimation();
	}
	
	public Drop(Vector pos, String prize, String spell) {
		this(pos, Droppable.fromStringWithSpell(prize, spell));
	}
	
	public Droppable getPrize() {
		if (prize instanceof RandomDrop) {
			return ((RandomDrop) prize).getDroppable();
		}
		return prize;
	}
	
	@Override
	public void onCollision(CollisionEvent e) {
		if (e.getTarget() instanceof Player) {
			((Player) e.getTarget()).add(getPrize());
			e.getWorld().delete(this);
		}
	}

	public String getPrizeString() {
		if (prize == null) {
			return "[null]";
		}
		return prize.toString();
	}

}
