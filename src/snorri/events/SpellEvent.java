package snorri.events;

import snorri.entities.Entity;
import snorri.main.GameWindow;
import snorri.world.Vector;
import snorri.world.World;

public class SpellEvent {

	private GameWindow window; //used to retrieve first, third, and world
	private Entity secondPerson; //the entity being affected by the spell
	
	private Vector loc; //the position in which or at which the spell is occuring
	private Vector dest; //the "target" position of the spell
	
	public SpellEvent(GameWindow window, Entity secondPerson) {
		this.secondPerson = secondPerson;
		this.window = window;
		
		loc = getFirstPerson().getPos().copy();
		dest = getThirdPerson().getPos().copy();
	}
	
	public SpellEvent(SpellEvent e) {
		secondPerson = e.secondPerson;
		window = e.window;
		
		loc = e.loc.copy();
		dest = e.dest.copy();
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
		return new Entity(window.getMousePos());
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
