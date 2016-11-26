package snorri.entities;

import snorri.animations.Animation;
import snorri.inventory.Inventory;

import snorri.world.Vector;

public class Crocodile extends LandMeleeUnit{

	private static final long serialVersionUID = 1L;
	private static final double APPROACH_MARGIN = 15;
	private static final double CHANGE_PATH_MARGIN = 350;

	public static final Animation IDLE = new Animation("/textures/animations/crocodile/idle");
	public static final Animation ATTACK = new Animation("/textures/animations/crocodile/attack");

	protected static double seekRange = 256;
	protected static double attackRange = 32;
	protected static double attackPower = 5; //TODO: change to something more logical and balanced

	protected Inventory inventory;
	protected Entity target;

	public Crocodile(Vector pos, Entity target) {
		super(pos, target, new Animation(IDLE), new Animation(IDLE), new Animation(ATTACK), seekRange, attackRange, attackPower, APPROACH_MARGIN, CHANGE_PATH_MARGIN);
		this.target = target;
		animation = new Animation(IDLE);
		inventory = new Inventory(this);
	}

	public Crocodile(Vector pos) {
		this(pos, null);
		animation = new Animation(IDLE);
	}

	/*public Crocodile(Vector pos, Entity target) {
		super(pos, new Animation(IDLE), new Animation(IDLE));
		this.target = target;
		inventory = new Inventory(this);
	}

	public Crocodile(Vector pos) {
		this(pos, null);
		animation = new Animation(IDLE);
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
		animation = ATTACK;
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
		lastSeenPos = target.pos.copy(); // don't put this in other thing
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
*/
}
