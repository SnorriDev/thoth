package snorri.semantics;

import snorri.main.Util;

/**
 * Use this class to take wrap any raw class types that are being used as concept Nouns.
 * This allows those nouns to implement Nominal, which means that they can have attributes like normal nouns.
 * @author snorri
 *
 */
public class ClassWrapper implements Nominal {

	public final Class<? extends Nominal> rawClass;
	
	public ClassWrapper(final Class<? extends Nominal> rawClass) {
		this.rawClass = rawClass;
	}
	
	@Override
	public String toString() {
		return "a " + Util.clean(rawClass.getSimpleName());
	}
	
}