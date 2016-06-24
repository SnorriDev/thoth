package snorri.semantics;

import snorri.entities.Projectile;
import snorri.entities.Unit;
import snorri.main.GamePanel;
import snorri.world.Vector;

public class Walk extends VerbDef {

	public Walk() {
		super(false);
	}

	@Override
	public boolean exec(Object obj) {
		if (e.getSecondPerson() instanceof Unit) {
			Vector trans = e.getDestination().copy().sub(e.getSecondPerson().getPos()).normalize();	
			((Unit) e.getSecondPerson()).walk(e.getWorld(), trans, GamePanel.getBaseDelta());
			return true;
		}
		return false;
	}

	//TODO: track if something is moving better
	
	/**
	 * @return whether or not an entity is moving
	 */
	@Override
	public boolean eval(Object subj, Object obj) {
		return subj instanceof Projectile;
	}
	
	@Override
	public boolean altersMovement() {
		return true;
	}

}
