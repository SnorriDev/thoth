package snorri.semantics;

import snorri.entities.Explosion;

public class Boom extends VerbDef {

	private static final double DAMAGE = 100;
	
	public Boom() {
		super(false);
	}

	//TODO make explosions bigger
	
	@Override
	public boolean exec(Object obj) {
		e.getSecondPerson().kill(e.getWorld());; //delete the thing that is exploding
		e.getWorld().add(new Explosion(e.getSecondPerson().getPos(), DAMAGE));
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return false;
	}

	@Override
	public String toString() {
		return "explode";
	}
	
	//TODO: boom whenever we return false?

}
