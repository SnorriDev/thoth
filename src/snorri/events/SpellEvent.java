package snorri.events;

import snorri.entities.Entity;
import snorri.world.Vector;

public class SpellEvent {

	private Entity firstPerson; //the player who is the source of the spell
	private Entity secondPerson; //the entity who is the focus of the spell
	private Entity thirdPerson; //the target of the spell
	
	private Vector loc; //the position in which or at which the spell is occuring
	private Vector dest; //the "target" position of the spell
	
	public SpellEvent(Entity firstPerson, Entity secondPerson, Entity thirdPerson) {
		this.firstPerson = firstPerson;
		this.secondPerson = secondPerson;
		this.thirdPerson = thirdPerson;
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
