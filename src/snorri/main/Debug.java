package snorri.main;

import snorri.events.SpellEvent;
import snorri.parser.Grammar;
import snorri.parser.Node;

public class Debug {

	public static final boolean ALL_HIEROGLYPHS_UNLOCKED = false;
	public static final boolean RENDER_TILE_GRID = false;
	public static final boolean LOG_FOCUS = false;
	public static final boolean LOG_WORLD = false;
	public static final boolean LOG_PARSES = false;
	public static final boolean LOG_RENDER_QUEUE = false;
	public static final boolean LOG_DAMAGE_EVENTS = false;
	public static final boolean LOG_DEATHS = false;
	public static final boolean SHOW_WEAPON_OUTPUT = false;
	public static final boolean SHOW_ORB_OUTPUT = false;
	public static final boolean DISABLE_PATHFINDING = false;
	public static final boolean SHOW_COLLIDERS = false;
	public static final boolean LOG_PAUSES = false;

	public static void castWTFMode(String s, SpellEvent e) {
		Node<?> spell = Grammar.parseString(s);
		Debug.log("\"" + s + "\": " + spell.getMeaning(e));
	}

	public static void castWTFMode(Node<?> spell, SpellEvent e) {
		Debug.log(spell + ": " + spell.getMeaning(e));
	}

	/**
	 * Use this to print temporary debugging messages to the game log.
	 * @param o
	 * 	the object to print
	 */
	public static void raw(Object o) {
		System.out.println("[RAW] " + o);
	}

	/**
	 * Use this to print long-term messages to the game log.
	 * @param s
	 * 	the string to print
	 */
	public static void log(String s) {
		System.out.println("[LOG] " + s);
	}

	/**
	 * Use this to print error messages to the game log.
	 * @param s
	 * 	the error string to print
	 */
	public static void error(String s) {
		System.err.println("[ERROR] " + s);
	}

}
