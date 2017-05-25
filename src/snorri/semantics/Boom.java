package snorri.semantics;

import snorri.entities.Explosion;
import snorri.events.SpellEvent;

public class Boom extends IntransVerbDef {

	private static final double DAMAGE = 100;
	
	public Boom() {
		super();
	}

	//TODO make explosions bigger
	
	@Override
	public boolean exec(SpellEvent e) {
		e.getSecondPerson().kill(e.getWorld()); //delete the thing that is exploding
		e.getWorld().add(new Explosion(e.getSecondPerson().getPos(), DAMAGE));
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
	
	//TODO: boom whenever we return false?

}
