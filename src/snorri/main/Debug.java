package snorri.main;

import snorri.events.SpellEvent;
import snorri.parser.Grammar;
import snorri.parser.Node;

public class Debug {

	public static void castWTFMode(String s, SpellEvent e) {
		Node spell = Grammar.parseString(s);
		Main.log("\"" + s + "\": " + spell.getMeaning(e));
	}
	
	public static void castWTFMode(Node spell, SpellEvent e) {
		Main.log(spell + ": " + spell.getMeaning(e));
	}
	
}
