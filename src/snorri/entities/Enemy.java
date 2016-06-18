package snorri.entities;

import java.util.ArrayDeque;

import snorri.inventory.Inventory;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.inventory.Orb;
import snorri.inventory.Weapon;
import snorri.main.Main;
import snorri.main.FocusedWindow;
import snorri.pathfinding.PathNode;
import snorri.pathfinding.Pathfinder;
import snorri.pathfinding.Pathfinding;
import snorri.world.Vector;
import snorri.world.World;

public class Enemy extends Unit implements Pathfinder {

	private static final long serialVersionUID = 1L;
	private static final double APPROACH_MARGIN = 15;
	private static final double CHANGE_PATH_MARGIN = 200;
	
	private Vector lastSeenPos;
	private boolean recalculatingPath = false;
	
	protected double seekRange = 1000;
	protected double attackRange = 300;
	
	protected Inventory inventory;
	protected Entity target;
	
	private ArrayDeque<PathNode> path;
	
	public Enemy(Vector pos, Entity target) {
		super(pos);
		this.target = target;
		inventory = new Inventory(this);
		setOrb((Orb) Item.newItem(ItemType.PELLET));
		setWeapon((Weapon) Item.newItem(ItemType.SLOW_SLING));
	}
	
	//can only use this when we are in GameWindow with Player
	public Enemy(Vector pos) {
		this(pos, ((FocusedWindow) Main.getWindow()).getWorld().getFocus());
	}
		
	public void setTarget(Entity target) {
		this.target = target;
	}
	
	public void setWeapon(Weapon weapon) {
		inventory.setWeapon(weapon);
	}
	
	public void setOrb(Orb orb) {
		inventory.setOrb(0, orb);
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
		while (tempPos.distanceSquared(pos) <= target.pos.distanceSquared(pos)) {	
		
			if (! world.getLevel().canShootOver(tempPos.copy().toGridPos())) {
				return false;
			}
			
			if (tempPos.distanceSquared(pos) > attackRange * attackRange) {
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
	public void update(World world, double deltaTime) {
		
		if (deltaTime > 1) {
			Main.error("got insane delta time of " + deltaTime);
		}
		
		if (target != null) {
			
			if (path != null) {
				
				if (target.pos.distanceSquared(lastSeenPos) > CHANGE_PATH_MARGIN * CHANGE_PATH_MARGIN) {
					stopPath(); //path will be recalculated if it's still in range
				}
				else if (canShootAt(world, target)) {
					shootAt(world, target);
				}
				else {
					follow(world, deltaTime);
				}
				
			}
			else if (target.pos.distanceSquared(pos) <= seekRange * seekRange && ! recalculatingPath) {
				startPath();
			}
						
		}
		
		inventory.update(deltaTime);
		super.update(world, deltaTime);
		
	}

	private void follow(World world, double deltaTime) {
		
		if (path.peek().getGlobalPos().distance(this.getPos()) < APPROACH_MARGIN) {
			path.pop();
		}
		
		if (path.isEmpty()) {
			stopPath();
			return;
		}
		
		walkTo(world, path.peek().getGlobalPos(), deltaTime);
		
	}

	@Override
	public void setPath(ArrayDeque<PathNode> stack) {
		if (stack != null) {
			path = stack;
		}
		recalculatingPath = false;
	}
	
	public void startPath() {
		
		if (pos == null) {
			return;
		}
				
		Pathfinding.setPathAsync(pos.copy().toGridPos(), target.pos.copy().toGridPos(), this);
		lastSeenPos = target.pos.copy(); //don't put this in other thing
		recalculatingPath = true;
	}
	
	public void stopPath() {
		path = null;
		recalculatingPath = false;
	}

}
