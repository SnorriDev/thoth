package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.parser.Node;
import snorri.world.Vector;
import snorri.world.World;

public class Break extends TransVerbDef {

	/**
	 * Only use this interface with Entities
	 * @author lambdaviking
	 */
	public interface Smashable {
		
		default void smash(World world, double deltaTime) {
			world.delete((Entity) this);
		}
		
	}
	
	public Break() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		
		Object obj = object.getMeaning(e);
		
		if (!(obj instanceof Entity)) {
			return false;
		}
		
		Entity target = (Entity) obj;
		
		if (target instanceof Smashable) {
			((Smashable) target).smash(e.getWorld(), e.getDeltaTime());
			return true;
		}
		
		//TODO make this open doors that are locked
		Vector tilePos = ((Entity) obj).getPos().copy().toGridPos();
		return Open.openDoor(e.getWorld(), tilePos);
		
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		return (Entity) obj instanceof Smashable;
	}

	@Override
	public String toString() {
		return "break";
	}

}
