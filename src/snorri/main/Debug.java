package snorri.main;

import java.awt.Color;
import java.util.Random;

import snorri.events.SpellEvent;
import snorri.parser.Grammar;
import snorri.parser.Node;

public class Debug {

	public static final boolean ALL_HIEROGLYPHS_UNLOCKED = false;
	public static final boolean RENDER_GRAPHS = false;
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
	public static final boolean HIDE_MASKS = false;
	
	private static final Color[] GRAPH_COLORS;
	
	static {
		GRAPH_COLORS = new Color[500];
		Random r = new Random();
		for (int i = 0; i < GRAPH_COLORS.length; i++) {
			GRAPH_COLORS[i] = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
		}
	}
	
	public static Color getColor(Object o) {
		if (o == null) {
			return Color.WHITE;
		}
		try {
			return GRAPH_COLORS[Util.niceMod(o.hashCode(), GRAPH_COLORS.length)];
		} catch (java.util.ConcurrentModificationException e) {
			return Color.WHITE;
		}
	}

	public static void castWTFMode(String s, SpellEvent e) {
		Node spell = Grammar.parseString(s);
		Main.log("\"" + s + "\": " + spell.getMeaning(e));
	}

	public static void castWTFMode(Node spell, SpellEvent e) {
		Main.log(spell + ": " + spell.getMeaning(e));
	}

}
