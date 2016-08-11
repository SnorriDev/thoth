package snorri.semantics;

import snorri.triggers.Trigger.TriggerType;

public class Pray extends VerbDef {

	public Pray() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		if (obj instanceof String) {
			TriggerType.PRAY.activate(obj);
			return true;
		}
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return obj instanceof String && TriggerType.PRAY.contains(obj);
	}

	@Override
	public String toString() {
		return "pray";
	}

}
