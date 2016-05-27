package snorri.events;

import snorri.entities.Entity;
import snorri.world.Vector;
import snorri.world.World;

public class SpellEvent {

	private Entity firstPerson; //the player who is the source of the spell
	private Entity secondPerson; //the entity who is the focus of the spell
	private Entity thirdPerson; //the target of the spell
	
	private World world;
	
	private Vector loc; //the position in which or at which the spell is occuring
	private Vector dest; //the "target" position of the spell
	
	public SpellEvent(Entity firstPerson, Entity secondPerson, Entity thirdPerson, World world) {
		this.firstPerson = firstPerson;
		this.secondPerson = secondPerson;
		this.thirdPerson = thirdPerson;
		this.world = world;
		loc = firstPerson.getPos().copy();
		dest = thirdPerson.getPos().copy();
	}
	
	public SpellEvent(SpellEvent e) {
		firstPerson = e.firstPerson;
		secondPerson = e.secondPerson;
		thirdPerson = e.thirdPerson;
		loc = e.loc.copy();
		dest = e.dest.copy();
	}
	
	public World getWorld() {
		return world;
	}
	
	public Entity getFirstPerson() {
		return firstPerson;
	}
	
	public Entity getSecondPerson() {
		return secondPerson;
	}
	
	public Entity getThirdPerson() {
		return thirdPerson;
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
	
}
