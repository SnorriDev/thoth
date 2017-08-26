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
	public static final boolean DISABLE_ANTIALIASING = true;
	
	private static final Logger logger;
	
	static {
		
		logger = Logger.getGlobal();
		
//		logger = Logger.getLogger("Thoth");
		logger.setLevel(Level.ALL);
		
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
		
//		for (Handler h : logger.getHandlers()) {
//			h.setLevel(Level.ALL);
//		}
		
//		System.setErr(new PrintStream(System.err) {
//			public void print(final String string) {
//				error(string);
//			}
//		});
		
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
	 * @param s
	 * 	the error string to print
	 */
	public static void error(Throwable e) {
		error(e.getMessage(), e);
	}
	
	public static void error(String msg, Throwable e) {
		logger.log(Level.SEVERE, msg, e);
	}
	
	@Deprecated
	public static void error(String msg) {
		logger.log(Level.SEVERE, msg);
	}
	
	/**
	 * Use this to print warning messages to the game log.
	 * @param s
	 * the warning string to print
	 */
	public static void warning(String s) {
		logger.log(Level.WARNING, s);
	}
	
}
