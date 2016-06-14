package snorri.entities;

import java.util.ArrayDeque;

import snorri.inventory.Inventory;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.inventory.Orb;
import snorri.inventory.Weapon;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.pathfinding.PathNode;
import snorri.pathfinding.Pathfinder;
import snorri.pathfinding.Pathfinding;
import snorri.world.Vector;
import snorri.world.World;

public class Enemy extends Unit implements Pathfinder {

	private static final long serialVersionUID = 1L;
	private static final double APPROACH_MARGIN = 15;
	
	protected double attackRange = 200;
	
	protected Inventory inventory;
	protected Entity target;
	
	private ArrayDeque<PathNode> path;
	
	public Enemy(Vector pos) {
		super(pos);
		inventory = new Inventory(this);
		inventory.addProjectile((Orb) Item.newItem(ItemType.PELLET));
		inventory.setWeapon((Weapon) Item.newItem(ItemType.SLING));
		Pathfinding.setPathAsync(pos.copy().toGridPos(), new Vector(100, 100).toGridPos(), this);
	}
	
	/**
	 * does a grid raycast to detect unpathable terrain blocking the shot
	 * @param target
	 * 		the entity we're tryna cap
	 * @return
	 * 		whether terrain obstructs the shot
	 */
	public boolean canShootAt(World world, Entity target) {
		
		Vector step = target.pos.copy().sub(pos).normalize();
		Vector tempPos = pos.copy();
		
		//I'm checking if pos and target.pos are both okay just in case we're in a wall
		while (tempPos.distanceSquared(pos) <= target.pos.distanceSquared(pos) && tempPos.distanceSquared(pos) <= attackRange * attackRange) {	
			if (world.getLevel().isPathable(tempPos)) {
				return false;
			}
			tempPos.add(step);	
		}

		return true;
		
	}
	
	public void shootAt(World world, Entity e) {
		if (inventory == null) {
			return;
		}
		inventory.tryToShoot(world, this, Vector.ZERO.copy(), e.getPos().copy().sub(pos));
	}
	
	@Override
	public void update(World world, float deltaTime) {
		
		if (path != null) {
			follow(world, deltaTime);
		}
		
		super.update(world, deltaTime);
		
	}

	private void follow(World world, float deltaTime) {
		
		if (path.peek().getGlobalPos().distance(this.getPos()) < APPROACH_MARGIN) {
			path.pop();
		}
		
		if (path.isEmpty()) {
			stopPath();
			shootAt(world, ((GameWindow) Main.getWindow()).getFocus());
			return;
		}
		
		walkTo(world, path.peek().getGlobalPos());
		
	}

	@Override
	public void setPath(ArrayDeque<PathNode> stack) {
		
		if (stack == null) {
			return;
		}
		
		path = new ArrayDeque<PathNode>();
		
		while (! stack.isEmpty()) {
			path.push(stack.poll());
		}
		
	}
	
	public void stopPath() {
		path = null;
	}

}
