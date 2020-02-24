package snorri.dialog;

import java.awt.Graphics;

import snorri.inventory.Droppable;
import snorri.windows.GameWindow;

public class DropMessage extends Message {
	
	private final Droppable drop;
	
	public DropMessage(Droppable drop) {
		super(drop.getTexture());
		this.drop = drop;
	}
	
	@Override
	public String toString() {
		return drop.toString() + " acquired";
	}
	
	/**
	 * @return the height of the line
	 */
	public int render(GameWindow window, Graphics gr, int xTrans) {
		if (hasIcon()) {
			return drawLineWithIcon(" acquired", gr, window, xTrans);
		}
		return super.render(window, gr, xTrans);
	}
	
}
