package snorri.semantics;

import snorri.main.Util;
import snorri.world.World;

public interface Nominal {
	
	public enum AbstractSemantics implements Nominal {
		
		POSITION, WEAPON, TILE, NAME, SOURCE, HEALTH, FLOOD, STORM;

		@Override
		public String toString() {
			return Util.clean(name());
		}
		
	}
	
	public static class NameConstant implements Nominal {
		
		private final String name;
		
		public NameConstant(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}

	default Object get(World world, AbstractSemantics attr) {
		
		if (attr == AbstractSemantics.NAME) {
			return new NameConstant(toString());
		}
		
		return null;
		
	}
	
}
