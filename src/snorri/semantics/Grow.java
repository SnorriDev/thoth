package snorri.semantics;

import java.lang.reflect.InvocationTargetException;

import snorri.entities.Entity;
import snorri.entities.Plant;
import snorri.main.Main;
import snorri.world.Vector;

public class Grow extends VerbDef {

	public Grow() {
		super(true);
	}

	@Override @SuppressWarnings("unchecked")
	public boolean exec(Object obj) {
		
		if (obj instanceof Class<?> && Plant.class.isAssignableFrom((Class<?>) obj)) {
			try {
				Entity ent = (Entity) ((Class<? extends Entity>) obj).getConstructor(Vector.class).newInstance(e.getLocative());
				e.getWorld().add(ent);
				return true;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				Main.error("could not instantiate entity type " + obj.toString());
				e.printStackTrace();
			}
		}
		
		return false;
		
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return false;
	}

	@Override
	public String toString() {
		return "grow";
	}

}
