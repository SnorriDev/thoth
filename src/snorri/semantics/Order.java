package snorri.semantics;

import snorri.entities.Vortex;
import snorri.events.CastEvent;

/**
 * Class holding complex semantics for the concept mAAt ("order")
 * @author snorri
 *
 */
public class Order implements Nominal {

	private static final long serialVersionUID = 1L;

	@Override
	public Nominal get(AbstractSemantics attr, CastEvent e) {
		
		switch(attr) {
		
		case STORM:
			return new ClassWrapper(Vortex.class);
			
		default:
			return Nominal.super.get(attr, e);
			
		}
				
	}
	
	@Override
	public String toString() {
		return "Ma'at (Order)";
	}
	
}
