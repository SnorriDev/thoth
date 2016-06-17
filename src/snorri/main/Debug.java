package snorri.main;

import snorri.events.SpellEvent;
import snorri.parser.Grammar;
import snorri.parser.Node;

public class Debug {

	public static boolean SHOW_WEAPON_OUTPUT = false;
	public static boolean SHOW_ORB_OUTPUT = false;

	public static void castWTFMode(String s, SpellEvent e) {
		Node spell = Grammar.parseString(s);
		Main.log("\"" + s + "\": " + spell.getMeaning(e));
	}

	public static void castWTFMode(Node spell, SpellEvent e) {
		Main.log(spell + ": " + spell.getMeaning(e));
	}

}
