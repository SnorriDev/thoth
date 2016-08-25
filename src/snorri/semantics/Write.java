package snorri.semantics;

import snorri.dialog.SpellMessage;
import snorri.main.GameWindow;
import snorri.main.Main;

public class Write extends VerbDef {

	public Write() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		if (Main.getWindow() instanceof GameWindow) {
			((GameWindow) Main.getWindow()).showMessage(new SpellMessage(obj));
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
		return "write";
	}

}
