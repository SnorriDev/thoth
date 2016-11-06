package snorri.entities;

import java.util.ArrayDeque;

import snorri.animations.Animation;
import snorri.collisions.RectCollider;
import snorri.inventory.Carrier;
import snorri.inventory.Inventory;
import snorri.pathfinding.PathNode;
import snorri.pathfinding.Pathfinder;
import snorri.pathfinding.Targetter;
import snorri.world.Vector;

public class Crocodile extends Unit implements Pathfinder, Carrier, Targetter {

	private static final long serialVersionUID = 1L;
	
	public static final Animation IDLE = new Animation("/textures/animations/crocodile/idle");
	public static final Animation ATTACK = new Animation("/textures/animations/crocodile/attack");
	
	public Crocodile(Vector pos) {
		super(pos, new RectCollider(143, 57));
		animation = new Animation(IDLE);
	}

	@Override
	public void setTarget(Entity target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Inventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPath(ArrayDeque<PathNode> stack) {
		// TODO Auto-generated method stub
		
	}

}
