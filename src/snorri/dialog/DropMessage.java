package snorri.dialog;

import java.awt.Graphics;

import snorri.inventory.Droppable;
import snorri.main.GameWindow;

public class DropMessage extends Message {
	
	private final Droppable drop;
	
	public DropMessage(Droppable drop) {
		super();
		this.drop = drop;
	}
	
	@Override
	public String toString() {
		return drop.toString() + " acquired";
	}
	
	/**
	 * @return the height of the line
	 */
	@Override
	public int render(GameWindow window, Graphics gr, int xTrans) {
		if (drop.getTexture() == null) {
			return drawLine(gr, window, xTrans);
		} else {
			return drawLineWithIcon(drop.getTexture(), " acquired", gr, window, xTrans);
		}
	}	
}
