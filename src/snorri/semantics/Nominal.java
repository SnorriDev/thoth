package snorri.semantics;

import snorri.main.Util;
import snorri.world.World;

public interface Nominal {
	
	public enum AbstractSemantics implements Nominal {
		
		POSITION, WEAPON, TILE, NAME, SOURCE, HEALTH, FLOOD, STORM, WALLOF;

		@Override
		public String toString() {
			return Util.clean(name());
		}
		
	}

	default Object get(World world, AbstractSemantics attr) {
		
		if (attr == AbstractSemantics.NAME) {
			return toString();
		}
		
		return null;
		
	}
	
}
