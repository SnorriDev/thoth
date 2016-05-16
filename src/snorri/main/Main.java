package snorri.main;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import snorri.entities.Entity;
import snorri.entities.EntityGroup;
import snorri.nonterminals.Sentence;
import snorri.parser.Grammar;
import snorri.parser.Node;
import snorri.parser.NonTerminal;
import snorri.parser.Rule;
import snorri.world.Position;

@SuppressWarnings("unused")
public class Main {

	public static void main(String[] args) {
		
		NonTerminal result = Grammar.parseString("sDm   jAm");
		System.out.println("Parse found: " + result);
		
		EntityGroup col = new EntityGroup();
		col.insert(new Entity(new Position(100, 100), 2));
		col.insert(new Entity(new Position(105, 130), 2));
		col.insert(new Entity(new Position(115, 100), 2));
		col.insert(new Entity(new Position(143, 133), 2));
		col.insert(new Entity(new Position(100, 124), 2));
		col.insert(new Entity(new Position(111, 130), 2));
		col.insert(new Entity(new Position(300, 130), 4));
					
		JFrame frame = new JFrame("Spoken Word");
		frame.getContentPane().add(new GameWindow(), BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);
		
		col.renderHitbox(frame.getGraphics());
		col.traverse();
		
	}

}
