package snorri.semantics;

import snorri.entities.Unit;
import snorri.events.CastEvent;
import snorri.parser.Node;

public class Damage extends TransVerbDef {

	private static final int AMOUNT = 30;
	
	public Damage() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, CastEvent e) {
		
		Object obj = object.getMeaning(e);
		
		if (obj instanceof Unit) {
			((Unit) obj).damage(AMOUNT, e);
		}
		return false;
		
	}

	@Override
	public boolean eval(Object subj, Object obj, CastEvent e) {
		return false;
	}

	@Override
	public String toString() {
		return "hurt";
	}

}
