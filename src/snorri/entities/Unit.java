package snorri.entities;

import java.util.ArrayList;
import java.util.List;

import snorri.animations.Animation;
import snorri.audio.ClipWrapper;
import snorri.collisions.Collider;
import snorri.collisions.RectCollider;
import snorri.events.CollisionEvent;
import snorri.events.SpellEvent;
import snorri.inventory.Carrier;
import snorri.inventory.Inventory;
import snorri.inventory.Stats;
import snorri.main.Debug;
import snorri.modifiers.Modifier;
import snorri.pathfinding.Team;
import snorri.semantics.Break;
import snorri.semantics.Go.Walker;
import snorri.semantics.Nominal;
import snorri.semantics.Wrapper;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Unit extends Entity implements Carrier, Walker {

	private static final long serialVersionUID = 1L;
	private static final int BASE_SPEED = 120;
	/**Dimensions for humanoid units*/
	public static final int RADIUS = 46, RADIUS_X = 21, RADIUS_Y = 40;
	
	protected List<Modifier<Unit>> modifiers = new ArrayList<>();
	
	protected int speed;
	private Team team;
	private Inventory inventory;
	protected Stats stats;
	protected double health;
	
	protected Animation walkingAnimation;
	protected Animation idleAnimation;
	protected Animation attackAnimation;
	
	protected String[] speechSounds;
	protected String[] damageSounds;
	protected String[] deathSounds;

	/**
	 * Use this constructor to build non-humanoid units
	 * @param pos
	 * 	Position to spawn the unit at
	 * @param c
	 * 	Collider for the unit
	 */
	protected Unit(Vector pos, Collider c) {
		super(pos, c);
		inventory = new Inventory(this);
		stats = new Stats(this);
		health = stats.getMaxHealth();
		z = UNIT_LAYER;
	}
	
	protected Unit(Vector pos, Collider collider, Animation idle, Animation walking) {
		this(pos, collider);
		initializeAnimations(idle, walking, idle); //TODO attack animations
	}
	
	protected Unit(Vector pos, Animation idle, Animation walking) {
		this(pos, new RectCollider(new Vector(2 * RADIUS_X, 2 * RADIUS_Y)), idle, walking);
	}

	@Override
	public void update(World world, double deltaTime) {
		inventory.update(deltaTime);
		
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
		
		// If the unit is standing on a trip wire, cut it.
		Break.tryToCutTripWire(world, pos.gridPos());
		
		if (isDead()) {
			world.delete(this);
			if (Debug.deathsLogged()) {
				Debug.logger.info(tag + " died.");
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

	/** Translate the position by delta. */
	@Override
	public void walk(World world, Vector delta) {
		moveNicely(world, delta.copy().multiply_(getSpeed()));
	}
	
	/** Walk in the direction dir with magnitude controlled by deltaTime. */
	@Override
	public void walkNormalized(World world, Vector dir, double deltaTime) {
		setAnimation(dir);
		Walker.super.walkNormalized(world, dir, deltaTime);
	}
	
	/** Walk towards a target position. */
	public void walkTowards(World world, Vector target, double deltaTime) {
		walkNormalized(world, target.copy().sub_(pos), deltaTime);
	}
	
	// The order of target and world are reversed here to be consistent with the newer AIAgent API.
	public void walkTowards(Entity target, World world, double deltaTime) {
		walkTowards(world, target.getPos(), deltaTime);
	}
	
	public double getHealth() {
		return health;
	}
	
	public void damage(double d) {
		if (Debug.damageEventsLogged()) {
			Debug.logger.info(this + "(" + (int) health + "/" + stats.getMaxHealth() + ") took " + d + " damage.");
		}
		health -= d;
	}
	
	public void damage(double d, SpellEvent e) {
		damage(e.modifyHealthInteraction(d));
	}
	
	public void heal(double d) {
		health = Math.min(health + d, stats.getMaxHealth());
	}
	
	public void heal(double d, SpellEvent e) {
		heal(e.modifyHealthInteraction(d));
	}
	
	public boolean isDead() {
		return health <= 0;
	}
	
	// Override this for faster entities.
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
	public Nominal get(AbstractSemantics attr, SpellEvent e) {
		if (attr == AbstractSemantics.HEALTH) {
			return new Wrapper<Integer>((int) health);
		}
		return super.get(attr, e);
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
	
	public void kill(World world) {
		damage(100);
		world.delete(this);
	}
	
	public String[] getSpeechSounds() {
		return speechSounds;
	}
	
	public String[] getdamageSounds() {
		return damageSounds;
	}
	
	public String[] getDeathSounds() {
		return deathSounds;
	}
	
	public String getSpeechSound() {
		if (speechSounds.length >= 1)
			return speechSounds[0];
		else
			return null;
	}
	
	public String getDamageSound() {
		if (damageSounds.length >= 1)
			return damageSounds[0];
		else
			return null;
	}
	
	public String getDeathSound() {
		if (deathSounds.length >= 1)
			return deathSounds[0];
		else
			return null;
	}
	
	public String getSpeechSound(int x) {
		if (x > speechSounds.length)
			return speechSounds[x];
		else
			return null;
	}
	
	public String getDamageSound(int x) {
		if (x > damageSounds.length)
			return damageSounds[x];
		else
			return null;
	}
	
	public String getDeathSound(int x) {
		if (x > deathSounds.length)
			return deathSounds[x];
		else
			return null;
	}
	
	public ClipWrapper getSpeechSoundClip() {
		if (speechSounds.length >= 1)
			return new ClipWrapper(speechSounds[0]);
		else
			return null;
	}
	
	public ClipWrapper getDamageSoundClip() {
		if (damageSounds.length >= 1)
			return new ClipWrapper(damageSounds[0]);
		else
			return null;
	}
	
	public ClipWrapper getDeathSoundClip() {
		if (deathSounds.length >= 1)
			return new ClipWrapper(deathSounds[0]);
		else
			return null;
	}
	
	public ClipWrapper getSpeechSoundClip(int x) {
		if (x > speechSounds.length)
			return new ClipWrapper(speechSounds[x]);
		else
			return null;
	}
	
	public ClipWrapper getDamageSoundClip(int x) {
		if (x > damageSounds.length)
			return new ClipWrapper(damageSounds[x]);
		else
			return null;
	}
	
	public ClipWrapper getDeathSoundClip(int x) {
		if (x > deathSounds.length)
			return new ClipWrapper(deathSounds[x]);
		else
			return null;
	}
	
	@Override
	public void onExplosion(CollisionEvent e) {
		damage(100);
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
}
