package snorri.main;

import java.util.Collection;

import snorri.entities.Entity;
import snorri.events.SpellEvent;
import snorri.parser.Grammar;
import snorri.parser.Node;

public class Debug {

	public static final boolean RENDER_TREE = false;
	public static final boolean LOG_FOCUS = false;
	public static final boolean LOG_WORLD = false;
	public static final boolean SHOW_WEAPON_OUTPUT = false;
	public static final boolean SHOW_ORB_OUTPUT = false;
	public static final boolean DISABLE_PATHFINDING = false;
	public static final boolean SHOW_COLLIDERS = false;

	public static void castWTFMode(String s, SpellEvent e) {
		Node spell = Grammar.parseString(s);
		Main.log("\"" + s + "\": " + spell.getMeaning(e));
	}

	public static void castWTFMode(Node spell, SpellEvent e) {
		Main.log(spell + ": " + spell.getMeaning(e));
	}

	public static boolean moreThanOnce(Collection<Entity> list, Entity searched) {

		int numCount = 0;
		boolean more = false;

		for (Entity thisNum : list) {
			if (thisNum == searched) {
				numCount++;
			}
		}

		if (numCount > 1) {
			more = true;
		}

		return more;
	}

}
