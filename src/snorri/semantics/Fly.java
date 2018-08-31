package snorri.semantics;

import snorri.events.SpellEvent;

public class Fly extends IntransVerbDef {

	// TODO(#31): Re-write flying for sidescrolling.
	
	public Fly() {
		super();
	}

	@Override
	public boolean exec(SpellEvent e) {
//		e.getSecondPerson().startFlying();
		return true;
	}

	@Override
	public boolean eval(Object subj, SpellEvent e) {
//		if (subj instanceof Entity) {
//			return ((Entity) subj).isFlying();
//		}
		return false;
	}

	@Override
	public String toString() {
		return "fly";
	}

}
