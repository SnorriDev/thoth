package snorri.semantics;

import snorri.entities.Vortex;
import snorri.world.World;

/**
 * Class holding complex semantics for the concept mAAt ("order")
 * @author snorri
 *
 */
//TODO this route and other route have lots of specialization
//upgrade this word over time?
public class Order implements Nominal {

	@Override
	public Object get(World world, AbstractSemantics attr) {
		
		switch(attr) {
		
		case STORM:
			return new ClassWrapper(Vortex.class);
			
		default:
			return Nominal.super.get(world, attr);
			
		}
				
	}
	
	@Override
	public String toString() {
		return "Ma'at (Order)";
	}
	
}
