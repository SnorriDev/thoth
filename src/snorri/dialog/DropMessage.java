package snorri.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import snorri.inventory.Droppable;
import snorri.main.GameWindow;
import snorri.world.Vector;

public class DropMessage extends Message {
	
	private final Droppable drop;
	
	public DropMessage(Droppable drop) {
		super();
		this.drop = drop;
	}
		
	/**
	 * @return the height of the line
	 */
	public int render(GameWindow window, Graphics gr, int xTrans) {
		Vector pos = window.getFocus().getHealthBarPos().add(0, TRANS_DOWN + xTrans);
		gr.setColor(Color.GREEN);
		Font oldFont = gr.getFont();
		gr.setFont(oldFont.deriveFont(FONT_SIZE));
		if (drop.getTexture() == null) {
			gr.drawString(drop.toString() + " acquired", pos.getX() + HEIGHT / 2, pos.getY() + HEIGHT / 2);
			return HEIGHT;
		} else {
			gr.drawImage(drop.getTexture(), pos.getX(), pos.getY(), null);
			gr.drawString(" acquired", drop.getTexture().getWidth(null) + pos.getX(), pos.getY() + drop.getTexture().getHeight(null) / 2);
			return drop.getTexture().getHeight(null) + IMAGE_BUFFER;
		}
	}	
}
