package snorri.entities;

import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Unit extends Entity {

	private static final long serialVersionUID = 1L;
	private static final int BASE_SPEED = 2;
	protected static final double MAX_HEALTH = 100;
	private static final double BURN_DOT = 5d;
	
	private double health;
	
	public Unit(Vector pos) {
		super(pos, 3);
		health = MAX_HEALTH;
	}
	
	public Unit(Unit unit) {
		super(unit);
		health = unit.health;
	}

	@Override
	public void update(World world, float deltaTime) {
		
		if (isBurning()) {
			damage(BURN_DOT * deltaTime);
			Main.log("burning");
		}
		
		if (isDead()) {
			world.delete(this);
		}
		
		super.update(world, deltaTime);
		
	}

	public void walk(Vector direction, EntityGroup col) {
		Vector dir = direction.copy();
		
		if (dir.equals(Vector.ZERO)) {
			return;
		}
		
		dir.multiply(getSpeed());
		col.move(this, dir);
	}
	
	public boolean wouldHitSomething(Vector direction, EntityGroup col) {
				
		if (direction.equals(Vector.ZERO)) {
			return true;
		}
		
		Unit clone = new Unit(this);
		clone.walk(direction, col);
		col.delete(clone);
		return col.getAllCollisions(clone).size() > 1;
	}
	
	public double getHealth() {
		return health;
	}
	
	public void damage(double d) {
		health -= d;
	}
	
	public boolean isDead() {
		return health <= 0;
	}
	
	//override this for faster entities
	protected int getSpeed() {
		return BASE_SPEED;
	}
	
}
