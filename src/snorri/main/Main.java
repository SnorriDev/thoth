package snorri.main;

import java.util.Arrays;

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
		
		EntityGroup world = new EntityGroup();
		
		Entity e1 = new Entity(new Position(20, 30), 10);
		Entity e2 = new Entity(new Position(40, 30), 10);
		Entity e3 = new Entity(new Position(25, 30), 10);
		
		System.out.println();
		world.insert(e1);
		world.traverse();
		
		System.out.println();
		world.insert(e2);
		world.traverse();
		
		System.out.println();
		world.insert(e3);
		world.traverse();
		
		System.out.println();
		world.delete(e3);
		world.traverse();
		
	}

}
