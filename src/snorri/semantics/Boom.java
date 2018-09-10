package snorri.semantics;

import snorri.entities.Entity;
import snorri.entities.Explosion;
import snorri.events.CastEvent;

public class Boom extends IntransVerbDef {

	private static final double DAMAGE = 100;
	
	public Boom() {
		super();
	}
	
	@Override
	public boolean exec(CastEvent e) {
		Entity secondPerson = e.getSecondPerson();
		if (secondPerson == null) {
			return false;
		}
		e.getWorld().delete(secondPerson);
		e.getWorld().add(new Explosion(secondPerson.getPos(), DAMAGE));
		return true;
	}

	@Override
	public boolean eval(Object subj, CastEvent e) {
		return false;
	}

	@Override
	public String toString() {
		return "explode";
	}
	
}
