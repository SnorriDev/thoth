package snorri.entities;

import java.util.ArrayList;

import snorri.animations.Animation;
import snorri.inventory.Carrier;
import snorri.inventory.Inventory;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.pathfinding.Pathfinder;
import snorri.pathfinding.Targetter;
import snorri.world.Vector;
import snorri.world.World;

public abstract class WaterMeleeUnit extends Unit implements Pathfinder, Carrier, Targetter {

	private static final long serialVersionUID = 1L;
	private static final double APPROACH_MARGIN_DEFAULT = 12;
	private static final double CHANGE_PATH_MARGIN_DEFAULT = 250;
	
	protected double approachMargin = APPROACH_MARGIN_DEFAULT;
	protected double changePathMargin = CHANGE_PATH_MARGIN_DEFAULT;
	
//	private Vector lastSeenPos;
//	private boolean recalculatingPath = false;

	private static final Animation IDLE_DEFAULT = new Animation("/textures/animations/crocodile/idle");
	private static final Animation SWIMMING_DEFAULT = new Animation("/textures/animations/crocodile/idle");
	private static final Animation ATTACK_DEFAULT = new Animation("/textures/animations/crocodile/idle");
	
	//protected Animation idle = IDLE_DEFAULT;
	//protected Animation walking = SWIMMING_DEFAULT;
	//protected Animation attack = ATTACK_DEFAULT;
	
	protected final double SEEKRANGE_DEFAULT = 128;
	protected final double ATTACKRANGE_DEFAULT = 16;
	protected final double ATTACKPOWER_DEFAULT = 4;
	
	private double seekRange = SEEKRANGE_DEFAULT;
	private double attackRange = ATTACKRANGE_DEFAULT;
	private double attackPower = ATTACKPOWER_DEFAULT; //TODO: change to something more logical and balanced

	protected Inventory inventory;
	protected Entity target;

//	private ArrayDeque<PathNode> path;
	
	
	public WaterMeleeUnit(Vector pos, Entity target, Animation idle, Animation walking, Animation attack, double seekRange, double attackRange, double attackPower, double approachMargin, double changePathMargin) {
		this(pos, target, idle, walking, attack, seekRange, attackRange, attackPower);
		
		this.approachMargin = approachMargin;
		this.changePathMargin = changePathMargin;
	}
	
	public WaterMeleeUnit(Vector pos, Entity target, Animation idle, Animation walking, double seekRange, double attackRange, double attackPower, double approachMargin, double changePathMargin) {
		this(pos, target, idle, walking, idle, seekRange, attackRange, attackPower, approachMargin, changePathMargin);
	}
	
	public WaterMeleeUnit(Vector pos, Entity target, Animation idle, double seekRange, double attackRange, double attackPower, double approachMargin, double changePathMargin) {
		this(pos, target, idle, idle, seekRange, attackRange, attackPower, approachMargin, changePathMargin);
	}
	
	public WaterMeleeUnit(Vector pos, Entity target, Animation idle, Animation walking, Animation attack, double seekRange, double attackRange, double attackPower) {
		super(pos, new Animation(idle), new Animation(walking), new Animation(attack));
		
		this.target = target;
		inventory = new Inventory(this);
		this.seekRange = seekRange;
		this.attackRange = attackRange;
		this.attackPower = attackPower;
	}
	
	public WaterMeleeUnit(Vector pos, Entity target, Animation idle, Animation walking, double seekRange, double attackRange, double attackPower) {
		this(pos, target, idle, walking, idle, seekRange, attackRange, attackPower);
	}
	
	public WaterMeleeUnit(Vector pos, Entity target, Animation idle, double seekRange, double attackRange, double attackPower) {
		this(pos, target, idle, idle, seekRange, attackRange, attackPower);
	}

	public WaterMeleeUnit(Vector pos, Entity target, Animation idle, Animation walking, Animation attack) {
		super(pos, new Animation(idle), new Animation(walking), new Animation(attack));
		
		this.target = target;
		inventory = new Inventory(this);
	}
	
	public WaterMeleeUnit(Vector pos, Entity target, Animation idle, Animation walking) {
		this(pos, target, idle, walking, idle);
	}
	
	public WaterMeleeUnit(Vector pos, Entity target, Animation idle) {
		this(pos, target, idle, idle);
	}
	
	public WaterMeleeUnit(Vector pos, Entity Target) {
		super(pos, new Animation(IDLE_DEFAULT), new Animation(SWIMMING_DEFAULT), new Animation(ATTACK_DEFAULT));
		animation = new Animation(idleAnimation);
	}

	public WaterMeleeUnit(Vector pos) {
		this(pos, null);
		animation = new Animation(idleAnimation);
	}

	@Override
	public void setTarget(Entity target) {
		this.target = target;

	}

	public boolean canAttack(World world, Entity target) {

		if (target.pos.distanceSquared(pos) > attackRange * attackRange) {
			return false;
		}

		Vector step = target.pos.copy().sub(pos).normalize();
		Vector tempPos = pos.copy();

		// I'm checking if pos and target.pos are both okay just in case we're
		// in a wall
		while (tempPos.distanceSquared(pos) <= target.pos.distanceSquared(pos)) {

			if (!world.getLevel().canShootOver(tempPos.copy().toGridPos())) {
				return false;
			}

			if (tempPos.distanceSquared(pos) > attackRange * attackRange) {
				return false;
			}

			tempPos.add(step);
		}

		return true;

	}

	public void attack(World world, Entity e) {
		animation = attackAnimation;
		((Unit)e).damage(attackPower);
	}

	// TODO don't walk into each other

	@Override
	public void update(World world, double deltaTime) {

		inventory.update(deltaTime);
		super.update(world, deltaTime);

		if (target == null) {
			if (Main.getWindow() instanceof GameWindow) {
				setTarget(((GameWindow) Main.getWindow()).getFocus());
			} else {
				return;
			}
		}

		if (canAttack(world, target)) {
			attack(world, target);
			return;
		}

		ArrayList<Vector> graph = world.getLevel().getGraph(this);
		ArrayList<Vector> targetGraph = world.getLevel().getGraph(target);

		//TODO: Implement Water Pathfinding
		/*if (path != null) {

			if (target.pos.distanceSquared(lastSeenPos) > changePathMargin * changePathMargin
					&& targetGraph == graph && graph != null) {
				stopPath();
			} else {
				follow(world, deltaTime);
			}

		} else if (!recalculatingPath && (pos.distanceSquared(target.pos) <= seekRange * seekRange)) {

			if (graph != null && graph == targetGraph) {
				startPath();
			}

		}*/

	}

	//TODO: implement water pathfinding
	/*private void follow(World world, double deltaTime) {

		if (path.peek().getGlobalPos().distance(this.getPos()) < approachMargin) {
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
		lastSeenPos = target.pos.copy(); // don't put this in other thing
		recalculatingPath = true;
	}

	public void stopPath() {
		path = null;
		recalculatingPath = false;
	}*/

	@Override
	public Inventory getInventory() {
		return inventory;
	
	}
}
