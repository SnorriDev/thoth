package snorri.semantics;

import snorri.entities.Projectile;
import snorri.world.Vector;

public class Walk extends VerbDef {

	public Walk() {
		super(false);
	}

	@Override
	public boolean exec(Object obj) {
		Vector trans = e.getDestination().copy().sub(e.getSecondPerson().getPos()).normalize();
		e.getWorld().getEntityTree().move(e.getSecondPerson(), trans);
		return true;
	}

	//TODO: track if something is moving better
	
	/**
	 * @return whether or not an entity is moving
	 */
	@Override
	public boolean eval(Object subj, Object obj) {
		return subj instanceof Projectile;
	}

}
