package snorri.semantics;

import snorri.entities.Explosion;

public class Boom extends VerbDef {

	public Boom() {
		super(false);
	}

	@Override
	public boolean exec(Object obj) {
		e.getWorld().add(new Explosion(e.getLocative()));
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return false;
	}
	
	//TODO: boom whenever we return false?

}
