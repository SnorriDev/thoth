package snorri.semantics;

import snorri.world.World;

public interface Nominal {

	public enum AbstractSemantics implements Nominal {
		POSITION, WEAPON, TILE, NAME, SOURCE;
	}

	default Object get(World world, AbstractSemantics attr) {
		
		if (attr == AbstractSemantics.NAME) {
			return toString();
		}
		
		return null;
		
	}
	
}
