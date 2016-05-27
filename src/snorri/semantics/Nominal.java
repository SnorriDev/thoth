package snorri.semantics;

import snorri.world.World;

public interface Nominal {

	public enum AbstractSemantics implements Nominal {
		POSITION, WEAPON, TILE, NAME;

		public Object get(World world, AbstractSemantics attr) {
			
			if (attr == NAME) {
				return this.toString();
			}
			
			return null;
		}
	}
	
	public Object get(World world, AbstractSemantics attr);
	
}
