package snorri.entities;

import snorri.events.SpellEvent;
import snorri.main.Main;
import snorri.world.Vector;
import snorri.world.World;

public class Unit extends Entity {

	private static final long serialVersionUID = 1L;
	private static final int BASE_SPEED = 100;
	public static final int RADIUS = 20;
	protected static final double MAX_HEALTH = 100;
	private static final double BURN_DOT = 8d;
	
	private double health;
	
	//for debugging purposes
	private boolean hasUpdated = false;
	
	public Unit(Vector pos) {
		super(pos, RADIUS);
		health = MAX_HEALTH;
	}
	
	public Unit(Unit unit) {
		super(unit);
		health = unit.health;
	}

	@Override
	public void update(World world, double deltaTime) {
		
		//TODO: remove need for this, also move to Entity
		if (hasUpdated) {
			Main.error("duplicate unit detected and removed");
			world.delete(this);
			return;
		}
		hasUpdated = true;
		
		if (isBurning()) {
			damage(BURN_DOT * deltaTime);
		}
		
		if (isDead()) {
			world.delete(this);
		}
		
		super.update(world, deltaTime);
		
	}
	
	public void resetUpdated() {
		hasUpdated = false;
	}

	public void walk(World world, Vector direction, double deltaTime) {
		moveHard(world, direction, getSpeed() * deltaTime);
	}
	
	public void walkTo(World world, Vector target, double deltaTime) {
		walk(world, target.copy().sub(pos), deltaTime);
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
