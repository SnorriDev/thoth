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
import snorri.nonterminals.NonTerminal;
import snorri.parser.Grammar;
import snorri.parser.Node;
import snorri.parser.Rule;
import snorri.world.Vector;
import snorri.world.World;

@SuppressWarnings("unused")
public class Main {
	
	private static GameWindow window;
	private static JFrame frame;
	
	public static void main(String[] args) {
		
		Node result = Grammar.parseString("sDm   jAm");
		Main.log("Parse found: " + result);
		
		//TODO: move the EntityGroup stuff into GameWindow
		
		World world = new World();
		
		Entity a = new Entity(new Vector(105, 130));
		Entity b = new Entity(new Vector(100, 100));
		Entity c = new Entity(new Vector(143, 133));
		Entity d = new Entity(new Vector(100, 124));
		Entity e = new Entity(new Vector(115, 100));
		Entity f = new Entity(new Vector(111, 130));
		world.add(a);
		world.add(b);
		world.add(c);
		world.add(d);
		world.add(e);
		world.add(f);
							
		window = new GameWindow(world, new Player(new Vector(50, 50)));
		world.add(window.getFocus()); //the player
		
		world.getEntityTree().traverse();
		
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
