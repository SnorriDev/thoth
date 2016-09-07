package snorri.dialog;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import snorri.main.GameWindow;
import snorri.world.Vector;

/**
 * A wrapper class to hold strings that explain what the player has to do.
 * Triggers should be used to control game logic, not this class.
 * Although it seems stupid, we need the empty constructor
 * and public fields for interaction with the YAML parser.
 * Use YAML's <code>|</code> modifier to store HTML lines.
 */

public class Objective extends Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String shortDesc = "";
	public String longDesc = "";
	
	public Objective() {
	}
	
	public void render(Graphics gr, GameWindow window) {
		Vector objPos = window.getFocus().getHealthBarPos().add(0, 33);
		drawLine(gr, window, objPos, Color.DARK_GRAY);
//		gr.drawString(shortDesc, objPos.getX(), objPos.getY());
	}

	@Override
	public String toString() {
		return shortDesc;
	}
	
}
