package snorri.events;

import snorri.entities.Entity;

public class SpellEvent {

	private Entity firstPerson; //the player who is the source of the spell
	private Entity secondPerson; //the entity who is the focus of the spell
	private Entity thirdPerson; //the target of the spell
	
	public SpellEvent(Entity firstPerson, Entity secondPerson, Entity thirdPerson) {
		this.firstPerson = firstPerson;
		this.secondPerson = secondPerson;
		this.thirdPerson = thirdPerson;
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
	
}
