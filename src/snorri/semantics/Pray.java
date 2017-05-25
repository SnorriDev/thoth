package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.parser.Node;
import snorri.triggers.Trigger.TriggerType;

public class Pray extends TransVerbDef {

	public Pray() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		Object obj = object.getMeaning(e);
		if (obj instanceof String) {
			TriggerType.PRAY.activate(obj);
			return true;
		}
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		return obj instanceof String && e.getWorld().getTriggerMap().contains(TriggerType.PRAY, obj);
	}

	@Override
	public String toString() {
		return "pray to (name)";
	}

}
