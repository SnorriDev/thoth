package snorri.semantics;

import java.lang.reflect.InvocationTargetException;

import snorri.entities.Entity;
import snorri.entities.Plant;
import snorri.events.SpellEvent;
import snorri.main.Debug;
import snorri.parser.Node;
import snorri.world.Vector;

public class Grow extends TransVerbDef {

	public Grow() {
		super();
	}

	@Override @SuppressWarnings("unchecked")
	public boolean exec(Node<Object> object, SpellEvent e) {
		
		Object meaning = object.getMeaning(e);
		if (!(meaning instanceof ClassWrapper)) {
			return false;
		}
		Class<?> obj = ((ClassWrapper) meaning).getValue();
		
		if (obj instanceof Class<?> && Plant.class.isAssignableFrom((Class<?>) obj)) {
			try {
				Entity ent = (Entity) ((Class<? extends Entity>) obj).getConstructor(Vector.class).newInstance(e.getLocative());
				e.getWorld().add(ent);
				return true;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e2) {
				Debug.error(e2);
			}
		}
		
		return false;
		
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		return false;
	}

	@Override
	public String toString() {
		return "grow";
	}

}
