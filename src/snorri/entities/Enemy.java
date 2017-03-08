package snorri.entities;

import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.List;

import snorri.animations.Animation;
import snorri.inventory.Carrier;
import snorri.inventory.Inventory;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.inventory.Orb;
import snorri.inventory.Weapon;
import snorri.main.Main;
import snorri.main.FocusedWindow;
import snorri.main.GameWindow;
import snorri.pathfinding.PathNode;
import snorri.pathfinding.Pathfinder;
import snorri.pathfinding.Pathfinding;
import snorri.pathfinding.Targetter;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Enemy extends Unit implements Pathfinder, Carrier, Targetter {

	private static final Animation IDLE = new Animation("/textures/animations/mummy/idle");
	private static final Animation WALKING = new Animation("textures/animations/mummy/walking");
	
	private static final long serialVersionUID = 1L;
	private static final double APPROACH_MARGIN = 15;
	private static final double CHANGE_PATH_MARGIN = 350;
	
	private Vector lastSeenPos;
	private boolean recalculatingPath = false;
	
	protected double seekRange = 1000;
	protected double attackRange = 450;
	
	protected Inventory inventory;
	protected Entity target;
	
	private ArrayDeque<PathNode> path;
	
	public Enemy(Vector pos, Entity target) {
		super(pos, IDLE, WALKING);
		this.target = target;
		inventory = new Inventory(this);
		getInventory().add(Item.newItem(ItemType.PELLET));
		getInventory().add(Item.newItem(ItemType.SLOW_SLING));
	}
	
	public Enemy(Vector pos) {
		this(pos, null);
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
		
		if (target.pos.distanceSquared(pos) > attackRange * attackRange) {
			return false;
		}
		
		Vector step = target.pos.copy().sub(pos).normalize();
		Vector tempPos = pos.copy();
		
		//I'm checking if pos and target.pos are both okay just in case we're in a wall
		while (tempPos.distanceSquared(pos) <= target.pos.distanceSquared(pos)) {	
		
			if (! world.canShootOver(tempPos)) {
				return false;
			}
			
			if (tempPos.distanceSquared(pos) > attackRange * attackRange) {
				return false;
			}
			
//			Entity col;
//			if ((col = world.getEntityTree().getFirstCollision(new Entity(tempPos))) != null
//					&& col != this && col != target) {
//				return false;
//			}
			
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
	
	//TODO don't walk into each other
	
	@Override
	public synchronized void update(World world, double deltaTime) {
		
		dontWalk();
		inventory.update(deltaTime);
		super.update(world, deltaTime);
		
		if (target == null) {
			if (Main.getWindow() instanceof GameWindow) {
				setTarget(((GameWindow) Main.getWindow()).getFocus());
			} else {
				return;
			}
		}
							
		if (canShootAt(world, target)) {
			shootAt(world, target);
			return;
		}
				
		List<Vector> graph = world.getComponent(this);
		List<Vector> targetGraph = world.getComponent(target);
		
		//Main.debug("comp size: " + graph.size());
		
		if (path != null) {
			
			if (target.pos.distanceSquared(lastSeenPos) > CHANGE_PATH_MARGIN * CHANGE_PATH_MARGIN
					&& targetGraph == graph && graph != null) {
				stopPath();
			} else {
				follow(world, deltaTime);
			}

		} else if (!recalculatingPath && (pos.distanceSquared(target.pos) <= seekRange * seekRange)) {
			
			if (graph != null && graph == targetGraph) {
				startPath();
			}

		}
						
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
		recalculatingPath = false;
		if (stack != null) {
			path = stack;
		}
	}
	
	public void recalculatePath() {
		recalculatingPath = true;
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

	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	@Override
	public void renderAround(FocusedWindow g, Graphics gr, double timeDelta) {
		
		//TODO debug this so that we follow paths correctly
		
		Vector p1 = pos.copy();
		
		if (path == null) {
			super.renderAround(g, gr, timeDelta);
			return;
		}
		
		for (PathNode n : path) {
			Vector p2 = n.getGlobalPos();
			Vector player = g.getFocus().getPos().copy().sub(g.getCenter().copy().add(Tile.WIDTH / 2, Tile.WIDTH / 2));
			Main.debug(p1.getX() - player.getX());
			gr.drawLine(p1.getX() - player.getX(), p1.getY() - player.getY(), p2.getX() - player.getX(), p2.getY() - player.getY());
			p1 = p2;
		}
		
		super.renderAround(g, gr, timeDelta);
	}

}
