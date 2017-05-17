package snorri.pathfinding;

import java.awt.Color;
import java.util.HashSet;
import java.util.Random;

import snorri.world.Vector;

public class Component extends HashSet<Vector> {

	private static final long serialVersionUID = 1L;
	private static Random r = new Random();

	private final Color color;
	
	public Component() {
		super();
		color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
	}
	
	public Color getColor() {
		return color;
	}

}
