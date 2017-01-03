package snorri.inventory;

import java.awt.Color;

import snorri.dialog.SpellMessage;
import snorri.entities.Unit;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.nonterminals.Sentence;

public class Papyrus extends Item {

	private static final long serialVersionUID = 1L;
	
	private static final Color PAPYRUS_COOLDOWN_COLOR = new Color(118, 45, 50, 150);
	
	public Papyrus(ItemType t) {
		super(t);
		timer = new Timer(5);
	}
	
	public boolean tryToActivate(Unit player) {
		
		if (timer.activate() && spell != null) {
			Object o = useSpellOn(player);
			if (Main.getWindow() instanceof GameWindow && spellIsStatement()) {
				((GameWindow) Main.getWindow()).showMessage(new SpellMessage(spell.getOrthography(), o));
			} else {
				Main.log(SpellMessage.format(spell.getOrthography(), o));
			}
			return true;
		}
		
		return false;
		
	}
	
	private boolean spellIsStatement() {
		return spell instanceof Sentence && ((Sentence) spell).isStatement();
	}
	
	@Override
	public Color getArcColor() {
		return PAPYRUS_COOLDOWN_COLOR;
	}
	
	@Override
	public int getInvPos() {
		return 3;
	}

}
