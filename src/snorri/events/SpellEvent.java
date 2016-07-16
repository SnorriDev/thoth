package snorri.events;

import snorri.entities.Entity;
import snorri.main.GameWindow;
import snorri.semantics.Nominal;
import snorri.world.Vector;
import snorri.world.World;

public class SpellEvent {
	
	private GameWindow window; //used to retrieve first, third, and world
	private Entity secondPerson; //the entity being affected by the spell
	
	private Vector loc; //the position in which or at which the spell is occuring
	private Vector dest; //the "target" position of the spell
	
	private Nominal instrument; //assigned by the preposition "with"
	
	private boolean negated = false; //used to keep track of negatives
	private int degree = 0; //only used for modifying adverbs; this is NOT a direct scaling factor on size/damage/etc.
	
	private double sizeModifier = 1; //modifies the size or magnitude of things within the world
	private double speedModifier = 1;//modifies velocities
	private double healthInteractModifier = 1; //used so that healing/damage effects aren't ridiculous on continuous casted spells
	
	public SpellEvent(GameWindow window, Entity secondPerson) {
		this.secondPerson = secondPerson;
		this.window = window;
		loc = getFirstPerson().getPos().copy(); //TODO getting this null at beginning
		dest = getThirdPerson().getPos().copy();
	}
	
	public SpellEvent(GameWindow window, Entity secondPerson, double healthInteractModifier) {
		this(window, secondPerson);
		this.healthInteractModifier = healthInteractModifier;
	}
	
	public SpellEvent(SpellEvent e) {
		
		window = e.window;
		secondPerson = e.secondPerson;
		
		loc = e.loc.copy();
		dest = e.dest.copy();
		
		instrument = e.instrument;
		
		negated = e.negated;
		degree = e.degree;
		
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
		return window.getWorld();
	}
	
	public Entity getFirstPerson() {
		return window.getFocus();
	}
	
	public Entity getSecondPerson() {
		return secondPerson;
	}
	
	public Entity getThirdPerson() {
		return new Entity(window.getMousePosAbsolute());
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
		return amount * healthInteractModifier;
	}
	
	/**
	 * use this to get modified speed boosts, etc.
	 * @param amount
	 * 	the amount before modifiers are applied
	 * @return
	 * 	the modified amount
	 */
	public double modifySpeed(double amount) {
		return amount * speedModifier;
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
	
}
