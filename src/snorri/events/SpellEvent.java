package snorri.events;

import snorri.collisions.Collider;
import snorri.collisions.RectCollider;
import snorri.entities.Entity;
import snorri.inventory.Carrier;
import snorri.main.Main;
import snorri.semantics.Chaos;
import snorri.semantics.Nominal;
import snorri.semantics.Order;
import snorri.world.Vector;
import snorri.world.World;

public class SpellEvent {
	
	private World world; //used to retrieve first, third, and world
	private Caster firstPerson; //the spell's caster
	private Entity secondPerson; //the entity being affected by the spell
	
	private Vector loc; //the position in which or at which the spell is occurring
	private Vector dest; //the "target" position of the spell
	
	private Nominal instrument; //assigned by the preposition "with"
	
	private boolean negated = false; //used to keep track of negatives
	private int degree = 0; //only used for modifying adverbs; this is NOT a direct scaling factor on size/damage/etc.
	
	private double deltaTime = 1; //used to dilute things on continuous spells
	
	private double sizeModifier = 1; //modifies the size or magnitude of things within the world
	private double speedModifier = 1;//modifies velocities
	private double healthInteractModifier = 1; //modifies healing/damage effects
	
	public interface Caster extends Carrier {
		
		public Vector getAimPosition();
		
	}
	
	public SpellEvent(World world, Caster firstPerson, Entity secondPerson) {
		this.firstPerson = firstPerson;
		this.secondPerson = secondPerson;
		this.world = world;
		loc = getSecondPerson().getPos().copy();
		dest = getThirdPerson().getPos().copy();
	}
	
	public SpellEvent(World world, Caster firstPerson, Entity secondPerson, double deltaTime) {
		this(world, firstPerson, secondPerson);
		this.deltaTime = deltaTime;
	}
	
	public SpellEvent(SpellEvent e) {
		
		world = e.world;
		firstPerson = e.firstPerson;
		secondPerson = e.secondPerson;
		
		loc = e.loc.copy();
		dest = e.dest.copy();
		
		instrument = e.instrument;
		
		negated = e.negated;
		degree = e.degree;
		
		deltaTime = e.deltaTime;
		
		sizeModifier = e.sizeModifier;
		speedModifier = e.speedModifier;
		healthInteractModifier = e.healthInteractModifier;
		
	}
	
	/**
	 * create a copy of this spell event with a different degree for adverb modification
	 */
	public SpellEvent(SpellEvent e, int degree) {
		this(e);
		this.degree = degree;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Caster getFirstPerson() {
		return firstPerson;
	}
	
	public Entity getFirstPersonEntity() {
		return (Entity) firstPerson;
	}
	
	public Entity getSecondPerson() {
		return secondPerson;
	}
	
	public Entity getThirdPerson() {
		Entity e = new Entity(firstPerson.getAimPosition());
		if (getWorld() == null) {
			return e;
		}
		Entity col = getWorld().getEntityTree().getFirstCollision(e, true);
		return col == null ? e : col;
	}
	
	public Vector getLocative() {
		return loc;
	}
	
	public Vector getDestination() {
		return dest;
	}
	
	public void setLocative(Vector v) {
		loc = v;
	}
	
	public void setDestination(Vector v) {
		dest = v;
	}

	public Object getInstrument() {
		return instrument;
	}

	public void setInstrument(Nominal instrument) {
		this.instrument = instrument;
	}
	
	/**
	 * use this to reduce healing and damage on continuous spells
	 * this method should also be used for controlling physical size
	 * (for example, the radius of an explosion)
	 * @param amount
	 * 	the amount before modifiers are applied
	 * @return
	 * 	the modified amount
	 */
	public double modifyHealthInteraction(double amount) {
		return amount * healthInteractModifier * deltaTime;
	}
	
	/**
	 * use this to get modified speed boosts, etc.
	 * @param amount
	 * 	the amount before modifiers are applied
	 * @return
	 * 	the modified amount
	 */
	public double modifySpeed(double amount) {
		return amount * speedModifier * deltaTime;
	}
	
	public double modifySize(double amount) {
		return amount * sizeModifier * deltaTime;
	}
	
	public SpellEvent scaleHealthInteractionModifier(double scale) {
		healthInteractModifier *= scale;
		return this;
	}
	
	public SpellEvent scaleSpeedModifier(double scale) {
		speedModifier *= scale;
		return this;
	}

	public SpellEvent getNegated() {
		SpellEvent copy = new SpellEvent(this);
		copy.negated = ! negated;
		return copy;
	}
	
	public boolean isNegated() {
		return negated;
	}
	
	/**
	 * use pollDegree instead so that degrees are reset to 0 for non-adverbs
	 * @return
	 * 	adverbial degree in this spell context
	 */
	@Deprecated
	public int getDegree() {
		return degree;
	}
	
	/**
	 * has the side effect of resetting the degree to 0 once it is read
	 * @return
	 * 	adverbial degree in this spell context
	 */
	public int pollDegree() {
		int degreeTemp = degree;
		degree = 0;
		return degreeTemp;
	}
	
	public double getDeltaTime() {
		return deltaTime;
	}
	
	//TODO scale entropy level by how good a player is or something?
	public int getEntropyLevel() {
		if (instrument instanceof Order) {
			return -1;
		}
		if (instrument instanceof Chaos) {
			return 1;
		}
		return 0;
	}
	
	public <E extends Entity> E resolveEntity(Class<E> c) {
		//TODO narrow the search range
		Collider collider = new RectCollider(new Vector(Main.getWindow()));
		Entity sweeper = new Entity(getSecondPerson().getPos(), collider);
		return world.getEntityTree().getFirstCollision(sweeper, c);
	}
	
}
