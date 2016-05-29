package snorri.main;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JFrame;

import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.parser.Lexicon;
import snorri.world.Vector;
import snorri.world.World;

public class Main {
	
	private static GameWindow window;
	private static JFrame frame;
	
	public static void main(String[] args) {
		
		Lexicon.init();
				
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
				
		frame = new JFrame("Spoken Word");
		frame.getContentPane().add(window, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);
		
	}
	
	public static Rectangle getBounds() {
		return frame.getBounds();
	}
	
	public static void error(Object o) {
		System.out.println("[ERROR] " + o);
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
