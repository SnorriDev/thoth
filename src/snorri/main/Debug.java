package snorri.main;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import snorri.events.SpellEvent;
import snorri.parser.Grammar;
import snorri.parser.Node;

public class Debug {
	
	private static final boolean ALL_HIEROGLYPHS_UNLOCKED = false;
	private static final boolean RENDER_TILE_GRID = false;
	private static final boolean LOG_FOCUS = false;
	private static final boolean LOG_WORLD = false;
	private static final boolean LOG_PARSES = false;
	private static final boolean LOG_RENDER_QUEUE = false;
	private static final boolean LOG_DAMAGE_EVENTS = false;
	private static final boolean LOG_DEATHS = false;
	private static final boolean SHOW_WEAPON_OUTPUT = false;
	private static final boolean SHOW_ORB_OUTPUT = false;
	private static final boolean DISABLE_PATHFINDING = false;
	private static final boolean SHOW_COLLIDERS = false;
	private static final boolean LOG_PAUSES = false;
	private static final boolean DISABLE_ANTIALIASING = true;
	private static final boolean WINDOWED_MODE = true;
	private static final boolean SCALE = false;
	private static final boolean DISABLE_MASKING = false;
	private static final boolean LOG_CHANGE_WORLD_EVENTS = true;
	private static final boolean LOG_PATHFINDING_COMPONENTS = false;
	
	private static final Logger logger;
	
	static {
		// TODO(lambdaviking): Call logger methods directly and replace format with:
		// "%1$tF %1$tT [%4$-7s][%2$s] %5$s %6$s%n".
		System.setProperty("java.util.logging.SimpleFormatter.format",
				"%1$tF %1$tT [%4$-7s] %5$s %6$s%n");
				
		logger = Logger.getLogger("Thoth");
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
		
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.ALL);
		logger.addHandler(consoleHandler);
				
		try {
			logger.addHandler(new FileHandler("logs/thoth.log"));
		} catch (SecurityException e) {
			System.out.println("no permission to open log file");
			e.printStackTrace();
		} catch (IOException e) {
			error(e);
		}	
	}
	
	// TODO make these all methods with side effects
	
	public static boolean allHieroglyphsUnlocked() {
		return ALL_HIEROGLYPHS_UNLOCKED;
	}
	
	public static boolean tileGridRendered() {
		return RENDER_TILE_GRID;
	}
	
	public static boolean focusLogged() {
		return LOG_FOCUS;
	}
	
	public static boolean worldLogged() {
		return LOG_WORLD;
	}
	
	public static boolean parsesLogged() {
		return LOG_PARSES;
	}
	
	public static boolean renderQueueLogged() {
		return LOG_RENDER_QUEUE;
	}
	
	public static boolean damageEventsLogged() {
		return LOG_DAMAGE_EVENTS;
	}
	
	public static boolean deathsLogged() {
		return LOG_DEATHS;
	}
	
	public static boolean weaponOutputLogged() {
		return SHOW_WEAPON_OUTPUT;
	}
	
	public static boolean orbOutputLogged() {
		return SHOW_ORB_OUTPUT;
	}
	
	public static boolean pathfindingDisabled() {
		return DISABLE_PATHFINDING;
	}
	
	public static boolean collidersRendered() {
		return SHOW_COLLIDERS;
	}
	
	public static boolean pausesLogged() {
		return LOG_PAUSES;
	}
	
	public static boolean antialiasingDisabled() {
		return DISABLE_ANTIALIASING;
	}
	
	public static boolean inWindowedMode() {
		return WINDOWED_MODE;
	}
	
	public static boolean scaled() {
		return SCALE;
	}
	
	public static boolean maskingDisabled() {
		return DISABLE_MASKING;
	}
	
	public static boolean changeWorldEventsLogged() {
		return LOG_CHANGE_WORLD_EVENTS;
	}
	
	public static boolean pathfindingComponentsLogged() {
		return LOG_PATHFINDING_COMPONENTS;
	}

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
		logger.log(Level.FINE, o == null ? null : o.toString());
	}

	/**
	 * Use this to print long-term messages to the game log.
	 * @param s
	 * 	the string to print
	 */
	public static void log(String s) {
		logger.log(Level.INFO, s);
	}

	/**
	 * Use this to print error messages to the game log.
	 * @param s The error string to print.
	 */
	public static void error(Throwable e) {
		error(e.getMessage(), e);
	}
	
	public static void error(String msg, Throwable e) {
		logger.log(Level.SEVERE, msg, e);
	}
	
	/**
	 * Use this to print warning messages to the game log.
	 * @param s The warning string to print.
	 */
	public static void warning(String s) {
		logger.log(Level.WARNING, s);
	}
	
}
