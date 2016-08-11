package snorri.semantics;

import snorri.entities.Explosion;

public class Boom extends VerbDef {

	private static final double DAMAGE = 100;
	
	public Boom() {
		super(false);
	}

	@Override
	public boolean exec(Object obj) {
		e.getWorld().add(new Explosion(e.getLocative(), e.modifyHealthInteraction(DAMAGE)));
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return false;
	}

	@Override
	public String toString() {
		return "boom!";
	}
	
	//TODO: boom whenever we return false?

}
