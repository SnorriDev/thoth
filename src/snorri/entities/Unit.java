package snorri.entities;

import snorri.events.SpellEvent;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Unit extends Entity {

	private static final long serialVersionUID = 1L;
	private static final int BASE_SPEED = 2;
	protected static final double MAX_HEALTH = 100;
	private static final double BURN_DOT = 8d;
	
	private double health;
	
	public Unit(Vector pos) {
		super(pos, 20);
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

	public void walk(World world, Vector direction) {
		move(world, direction, getSpeed());
	}
	
	public boolean wouldHitSomething(World world, Vector direction) {
				
		if (direction.equals(Vector.ZERO)) {
			return true;
		}
		
		Unit clone = new Unit(this);
		clone.walk(world, direction);
		world.getEntityTree().delete(clone);
		return world.getEntityTree().getAllCollisions(clone).size() > 1;
	}
	
	public double getHealth() {
		return health;
	}
	
	public void damage(double d) {
		health -= d;
	}
	
	public void damage(double d, SpellEvent e) {
		heal(e.modifyHealthInteraction(d));
	}
	
	public void heal(double d) {
		health += d;
		if (health > MAX_HEALTH) {
			health = MAX_HEALTH;
		}
	}
	
	public void heal(double d, SpellEvent e) {
		heal(e.modifyHealthInteraction(d));
	}
	
	public boolean isDead() {
		return health <= 0;
	}
	
	//override this for faster entities
	protected int getSpeed() {
		return BASE_SPEED;
	}
	
	@Override
	public Object get(World world, AbstractSemantics attr) {
		
		if (attr == AbstractSemantics.HEALTH) {
			return (int) health;
		}
		
		return super.get(world, attr);
		
	}
	
}
