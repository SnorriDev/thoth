package snorri.main;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.util.Arrays;

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
	
	public static void main(String[] args) {
		
		NonTerminal result = Grammar.parseString("sDm   jAm");
		System.out.println("Parse found: " + result);
		
		EntityGroup col = new EntityGroup();
		col.insert(new Entity(new Position(100, 100), 2));
		col.insert(new Entity(new Position(105, 130), 2));
		Entity e = new Entity(new Position(115, 100), 2);
		col.insert(e);
		col.insert(new Entity(new Position(143, 133), 2));
		col.insert(new Entity(new Position(100, 124), 2));
		col.insert(new Entity(new Position(111, 130), 2));
		col.insert(new Entity(new Position(300, 130), 4));	
		col.delete(e);
					
		JFrame frame = new JFrame("Spoken Word");
		window = new GameWindow(col, new Player(new Position(50, 50)));
		
		col.insert(window.getFocus()); //the player
		
		frame.getContentPane().add(window, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);
		
		col.traverse();
		
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
