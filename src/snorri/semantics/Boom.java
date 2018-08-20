package snorri.semantics;

import snorri.entities.Entity;
import snorri.entities.Explosion;
import snorri.events.SpellEvent;

public class Boom extends IntransVerbDef {

	private static final double DAMAGE = 100;
	
	public Boom() {
		super();
	}
	
	@Override
	public boolean exec(SpellEvent e) {
		Entity secondPerson = e.getSecondPerson();
		if (secondPerson == null) {
			return false;
		}
		e.getWorld().delete(secondPerson);
		e.getWorld().add(new Explosion(secondPerson.getPos(), DAMAGE));
		return true;
	}

	@Override
	public boolean eval(Object subj, SpellEvent e) {
		return false;
	}

	@Override
	public String toString() {
		return "explode";
	}
	
}
