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
	
	private static final long serialVersionUID = 1L;
	private static final double APPROACH_MARGIN = Tile.WIDTH; //was 15, was causing pathfinding bug?
	private static final double CHANGE_PATH_MARGIN = 350;
	
	private Vector lastSeenPos;
	private boolean recalculatingPath = false;
	
	protected double seekRange = 1000;
	protected double attackRange = 450;
	
	protected Inventory inventory;
	protected Entity target;
	
	private ArrayDeque<PathNode> path;
	
	protected Enemy(Vector pos, Entity target, Animation idle, Animation walking) {
		super(pos, idle, walking);
		this.target = target;
		inventory = new Inventory(this);
		getInventory().add(Item.newItem(ItemType.PELLET));
		getInventory().add(Item.newItem(ItemType.SLOW_SLING));
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
	public boolean canAttack(Entity target, World world) {
		return target.pos.distanceSquared(pos) < attackRange * attackRange;
	}
	
	public void attack(World world, Entity e) {
		if (inventory == null) {
			return;
		}
		inventory.attack(world, this, Vector.ZERO.copy(), e.getPos().copy().sub(pos));
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
							
		if (canAttack(target, world)) {
			attack(world, target);
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
						
		if (path == null) {
			super.renderAround(g, gr, timeDelta);
			return;
		}
		
		super.renderAround(g, gr, timeDelta);
	}

}
