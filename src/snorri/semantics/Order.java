package snorri.semantics;

import snorri.entities.Vortex;
import snorri.events.SpellEvent;

/**
 * Class holding complex semantics for the concept mAAt ("order")
 * @author snorri
 *
 */
//TODO this route and other route have lots of specialization
//upgrade this word over time?
public class Order implements Nominal {

	@Override
	public Object get(AbstractSemantics attr, SpellEvent e) {
		
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
