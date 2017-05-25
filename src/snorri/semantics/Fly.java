package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.SpellEvent;

public class Fly extends IntransVerbDef {

	public Fly() {
		super();
	}

	@Override
	public boolean exec(SpellEvent e) {
		e.getSecondPerson().startFlying();
		return true;
	}

	@Override
	public boolean eval(Object subj, SpellEvent e) {
		if (subj instanceof Entity) {
			return ((Entity) subj).isFlying();
		}
		return false;
	}

	@Override
	public String toString() {
		return "fly";
	}

}
