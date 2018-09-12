package snorri.semantics;

import java.io.Serializable;

import snorri.events.CastEvent;
import snorri.main.Util;

public interface Nominal extends Serializable {
	
	public enum AbstractSemantics implements Nominal {
		
		POSITION, WEAPON, TILE, NAME, SOURCE, HEALTH, FLOOD, STORM, ONE;

		@Override
		public String toString() {
			return Util.clean(name());
		}
		
	}
	
	public static class NameConstant implements Nominal {
		
		private static final long serialVersionUID = 1L;
		private final String name;
		
		public NameConstant(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}

	default Nominal get(AbstractSemantics attr, CastEvent e) {
		
		if (attr == AbstractSemantics.NAME) {
			return new NameConstant(toString());
		}
		
		return null;
		
	}
	
}
