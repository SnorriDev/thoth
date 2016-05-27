package snorri.semantics;

public interface Nominal {

	public enum AbstractSemantics implements Nominal {
		POSITION, WEAPON, TILE, NAME;

		public Object get(AbstractSemantics attr) {
			
			if (attr == NAME) {
				return this.toString();
			}
			
			return null;
		}
	}
	
	public Object get(AbstractSemantics attr);
	
}
