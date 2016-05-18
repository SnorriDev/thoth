package snorri.main;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import snorri.entities.Entity;
import snorri.entities.EntityGroup;
import snorri.entities.Player;
import snorri.nonterminals.Sentence;
import snorri.parser.Grammar;
import snorri.parser.Node;
import snorri.parser.NonTerminal;
import snorri.parser.Rule;
import snorri.world.Position;

@SuppressWarnings("unused")
public class Main {
	
	private static GameWindow window;
	private static JFrame frame;
	
	public static void main(String[] args) {
		
		NonTerminal result = Grammar.parseString("sDm   jAm");
		Main.log("Parse found: " + result);
		
		//TODO: move the EntityGroup stuff into GameWindow
		
		EntityGroup col = new EntityGroup();
		Entity a = new Entity(new Position(105, 130));
		Entity b = new Entity(new Position(100, 100));
		Entity c = new Entity(new Position(143, 133));
		Entity d = new Entity(new Position(100, 124));
		Entity e = new Entity(new Position(115, 100));
		Entity f = new Entity(new Position(111, 130));
		col.insert(a);
		col.insert(b);
		col.insert(c);
		col.insert(d);
		col.insert(e);
		col.insert(f);
							
		window = new GameWindow(col, new Player(new Position(50, 50)));
		col.insert(window.getFocus()); //the player
		
		col.traverse();
		
		frame = new JFrame("Spoken Word");
		frame.getContentPane().add(window, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);
		
	}
	
	public static Rectangle getBounds() {
		return frame.getBounds();
	}
	
	public static void log(String s) {
		System.out.println("[LOG] " + s);
		//System.out.println("[" + Colors.LOG + "LOG" + Colors.RESET + "] " + s);
	}
	
	public static void log(Object o) {
		System.out.println("[RAW] " + o);
	}
	
	public static GameWindow getWindow() {
		return window;
	}

}
