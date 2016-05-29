package snorri.parser;

import snorri.events.SpellEvent;
import snorri.main.Main;

public class Spell {

	public static void cast(String s, SpellEvent e) {
		
		Node spell = Grammar.parseString(s);
		Main.log(s + ": " + spell.getMeaning(e));
		
	}
	
}
