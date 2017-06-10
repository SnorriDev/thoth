package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.main.Util;

public interface Nominal {
	
	public enum AbstractSemantics implements Nominal {
		
		POSITION, WEAPON, TILE, NAME, SOURCE, HEALTH, FLOOD, STORM, ONE;

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

	default Object get(AbstractSemantics attr, SpellEvent e) {
		
		if (attr == AbstractSemantics.NAME) {
			return new NameConstant(toString());
		}
		
		return null;
		
	}
	
}
