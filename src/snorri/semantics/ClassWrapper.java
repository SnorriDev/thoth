package snorri.semantics;

import snorri.entities.Entity;
import snorri.events.CastEvent;
import snorri.main.Util;

/**
 * Use this class to take wrap any raw class types that are being used as concept Nouns.
 * This allows those nouns to implement Nominal, which means that they can have attributes like normal nouns.
 * @author snorri
 *
 */
public class ClassWrapper extends Wrapper<Class<? extends Nominal>> {

	private static final long serialVersionUID = 1L;
		
	public ClassWrapper(final Class<? extends Nominal> rawClass) {
		super(rawClass);
	}
	
	@Override
	public String toString() {
		return "a " + Util.clean(value.getSimpleName());
	}
	
	@Override
	public Nominal get(AbstractSemantics attr, CastEvent e) {
		
		switch (attr) {
		case ONE:
			return resolve(e);
		case POSITION:
			Entity ent = resolve(e);
			return (ent == null) ? null : ent.getPos();
		default:
			return super.get(attr, e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Entity resolve(CastEvent e) {
		if (Entity.class.isAssignableFrom(value)) {
			return e.resolveEntity((Class<? extends Entity>) value);
		}
		return null;
		
	}
	
}
