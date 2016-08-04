package snorri.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import snorri.inventory.Droppable;
import snorri.inventory.Timer;
import snorri.world.Vector;

public class DropMessage {

	private static final double LENGTH = 2.5;
	private static final int TRANS_DOWN = 46;
	private static final int HEIGHT = 23;
	private static final float FONT_SIZE = 23;
	
	private final Droppable drop;
	private final Timer timer;
	
	public DropMessage(Droppable drop) {
		this.drop = drop;
		timer = new Timer(LENGTH);
		timer.hardReset();
	}
	
	//TODO take image icon input
	
	public boolean update(double deltaTime) {
		timer.update(deltaTime);
		return timer.isOffCooldown();
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
			gr.drawString(drop.toString() + " acquired", pos.getX(), pos.getY());
			return HEIGHT;
		} else {
			gr.drawImage(drop.getTexture(), pos.getX(), pos.getY(), null);
			gr.drawString(" acquired", drop.getTexture().getWidth(null) + pos.getX(), pos.getY() + drop.getTexture().getHeight(null) / 2);
			return drop.getTexture().getHeight(null);
		}
	}	
}
