package snorri.entities;

import java.util.ArrayList;
import java.util.List;

import snorri.animations.Animation;
import snorri.collisions.Collider;
import snorri.collisions.RectCollider;
import snorri.events.SpellEvent;
import snorri.main.Debug;
import snorri.main.Main;
import snorri.modifiers.Modifier;
import snorri.pathfinding.Team;
import snorri.semantics.Walk.Walker;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Unit extends Entity implements Walker {

	private static final long serialVersionUID = 1L;
	private static final int BASE_SPEED = 120;
	/**Dimensions for humanoid units*/
	public static final int RADIUS = 46, RADIUS_X = 21, RADIUS_Y = 40;
	protected static final double MAX_HEALTH = 100;
	
	protected List<Modifier<Unit>> modifiers = new ArrayList<>();
	
	protected int speed;
	private Team team;
	private double health;
	
	protected Animation walkingAnimation;
	protected Animation idleAnimation;
	protected Animation attackAnimation;
		
	protected Unit(Vector pos, Animation idle, Animation walking) {
		this(pos, new RectCollider(new Vector(2 * RADIUS_X, 2 * RADIUS_Y)));
		initializeAnimations(idle, walking, idle);
	}
	
	protected Unit(Vector pos, Animation idle, Animation walking, Animation attack) {
		this(pos, new RectCollider(new Vector(2 * RADIUS_X, 2 * RADIUS_Y)));
		initializeAnimations(idle, walking, attack);
	}
		
	/**
	 * Use this constructor to build non-humanoid units
	 * @param pos
	 * 	Position to spawn the unit at
	 * @param c
	 * 	Collider for the unit
	 */
	protected Unit(Vector pos, Collider c) {
		super(pos, c);
		health = MAX_HEALTH;
		z = UNIT_LAYER;
	}

	@Override
	public void update(World world, double deltaTime) {
		
		speed = getBaseSpeed();
		
		if (modifiers == null) {
			modifiers = new ArrayList<>();
		}
		
		for (Object o : modifiers.toArray()) {
			@SuppressWarnings("unchecked")
			Modifier<Unit> m = (Modifier<Unit>) o;
			if (m.modify(this, deltaTime)) {
				modifiers.remove(m);
			}
		}
		
		if (isDead()) {
			world.delete(this);
			if (Debug.LOG_DEATHS) {
				Main.log(tag + " died");
			}
			TriggerType.KILL.activate(tag);
		}
		
		super.update(world, deltaTime);
		
	}
	
	/**
	 * Set the animations for this unit to copies of the supplied ones
	 * @param idle
	 * 	the idle animation
	 * @param walking
	 * 	the walking animation
	 * @param attack
	 * 	the attack animation
	 */
	protected void initializeAnimations(Animation idle, Animation walking, Animation attack) {
		idleAnimation = new Animation(idle);
		walkingAnimation = new Animation(walking);
		attackAnimation = new Animation(attack);
		animation = idleAnimation;
	}
	
	/**
	 * Update the animations on a unit which isn't walking
	 */
	public void dontWalk() {
		setAnimation(Vector.ZERO);
	}
	
	/**
	 * Set the walking or idle animation based on the current movement vector
	 * @param direction
	 * 	the direction of movement
	 */
	protected void setAnimation(Vector direction) {
		animation = (direction.magnitude() == 0) ? idleAnimation : walkingAnimation;
		if (direction.getX() != 0 && animation != null) {
			walkingAnimation.flip(direction.getX() > 0);
			idleAnimation.flip(direction.getX() > 0);
		}
	}

	@Override
	public void walk(World world, Vector delta) {
		moveNicely(world, delta.copy().multiply(getSpeed()));
	}
	
	@Override
	public void walkNormalized(World world, Vector dir, double deltaTime) {
		setAnimation(dir);
		Walker.super.walkNormalized(world, dir, deltaTime);
	}
	
	public void walkTo(World world, Vector target, double deltaTime) {
		walkNormalized(world, target.copy().sub(pos), deltaTime);
	}
	
	public double getHealth() {
		return health;
	}
	
	public void damage(double d) {
		if (Debug.LOG_DAMAGE_EVENTS) {
			Main.log(this + "(" + (int) health + "/" + MAX_HEALTH + ") took " + d + " damage");
		}
		health -= d;
	}
	
	public void damage(double d, SpellEvent e) {
		damage(e.modifyHealthInteraction(d));
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
	protected final int getSpeed() {
		return speed;
	}
	
	public int getBaseSpeed() {
		return BASE_SPEED;
	}
	
	public void setSpeed(int spd) {
		speed = spd;
	}
	
	public void modifySpeed(double factor) {
		speed = (int) (speed * factor);
	}
	
	@Override
	public Object get(World world, AbstractSemantics attr) {
		
		if (attr == AbstractSemantics.HEALTH) {
			return (int) health;
		}
		
		return super.get(world, attr);
		
	}
	
	public List<Modifier<Unit>> getModifiers() {
		return modifiers;
	}
	
	public boolean hasModifier(Class <? extends Modifier<Unit>> c) {
		for (Modifier<Unit> modifier : modifiers) {
			if (c.isInstance(modifier)) {
				return true;
			}
		}
		return false;
	}

	public void addModifier(Modifier<Unit> m) {
		modifiers.add(m);
	}
	
	public void removeModifier(Modifier<Unit> m){
		modifiers.remove(m);
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}
	
	public Team getTeam() {
		if (team == null) {
			Team out = new Team();
			out.add(this);
			return out;
		}
		return team;
	}
	
	@Override
	public void kill(World world) {
		damage(100);
		super.kill(world);
	}
	
}
