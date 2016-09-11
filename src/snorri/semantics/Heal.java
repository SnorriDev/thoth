package snorri.semantics;

import snorri.entities.Unit;
import snorri.triggers.Trigger.TriggerType;

public class Heal extends VerbDef {

	private static final double AMOUNT = 10;
	
	public Heal() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		if (obj instanceof Unit) {
			((Unit) obj).heal(AMOUNT, e);
			TriggerType.HEAL.activate(((Unit) obj).getTag());
			return true;
		}
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return false;
	}

	@Override
	public String toString() {
		return "heal";
	}

}
