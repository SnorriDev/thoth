package snorri.semantics;

import snorri.events.CastEvent;

public class Fly extends IntransVerbDef {

	// TODO(#31): Re-write flying for sidescrolling.
	
	public Fly() {
		super();
	}

	@Override
	public boolean exec(CastEvent e) {
//		e.getSecondPerson().startFlying();
		return true;
	}

	@Override
	public boolean eval(Object subj, CastEvent e) {
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
