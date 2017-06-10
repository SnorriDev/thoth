package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.main.Util;

/**
 * Use this class to take wrap any raw class types that are being used as concept Nouns.
 * This allows those nouns to implement Nominal, which means that they can have attributes like normal nouns.
 * @author snorri
 *
 */
public class ClassWrapper implements Nominal {

	public final Class<? extends Nominal> rawClass;
	
	public ClassWrapper(final Class<? extends Nominal> rawClass) {
		this.rawClass = rawClass;
	}
	
	@Override
	public String toString() {
		return "a " + Util.clean(rawClass.getSimpleName());
	}
	
	@Override
	public Object get(AbstractSemantics attr, SpellEvent e) {
		
		switch (attr) {
		case ONE:
			return resolve(e);
		case POSITION:
			Entity ent = resolve(e);
			return (ent == null) ? null : ent.getPos();
		default:
			return Nominal.super.get(attr, e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Entity resolve(SpellEvent e) {
		if (Entity.class.isAssignableFrom(rawClass)) {
			return e.resolveEntity((Class<? extends Entity>) rawClass);
		}
		return null;
		
	}
	
}
