package snorri.semantics;

import snorri.dialog.SpellMessage;
import snorri.events.SpellEvent;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.parser.Node;
import snorri.triggers.Trigger.TriggerType;

public class Write extends TransVerbDef {

	public Write() {
		super();
	}
	
	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		Object obj = object.getMeaning(e);
		if (Main.getWindow() instanceof GameWindow) {
			TriggerType.WRITE.activate((String) obj);
			((GameWindow) Main.getWindow()).showMessage(new SpellMessage(obj));
			return true;
		}
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		return false;
	}

	@Override
	public String toString() {
		return "write";
	}

}
