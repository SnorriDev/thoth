package snorri.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import snorri.inventory.Timer;
import snorri.main.GameWindow;
import snorri.world.Vector;

public abstract class Message {

	protected static final double LENGTH = 2.5;
	protected static final int TRANS_DOWN = 46;
	protected static final int HEIGHT = 28;
	protected static final int IMAGE_BUFFER = 5;
	protected static final float FONT_SIZE = 23;
	
	protected final Timer timer;
	
	protected boolean success = true;

	protected Message() {
		timer = new Timer(LENGTH);
		timer.hardReset();
	}
	
	public boolean update(double deltaTime) {
		timer.update(deltaTime);
		return timer.isOffCooldown();
	}
	
	protected Vector getPos(GameWindow window, int xTrans) {
		return window.getFocus().getHealthBarPos().add(0, TRANS_DOWN + xTrans);
	}
	
	public abstract String toString();
	
	public int render(GameWindow window, Graphics gr, int xTrans) {
		return drawLine(gr, window, xTrans);
	}
	
	protected int drawLine(Graphics gr, GameWindow window, int xTrans) {
		gr.setColor(success ? Color.GREEN : Color.RED);
		Font oldFont = gr.getFont();
		gr.setFont(oldFont.deriveFont(FONT_SIZE));
		Vector pos = getPos(window, xTrans);
		gr.drawString(this.toString(), pos.getX() + HEIGHT / 2, pos.getY() + HEIGHT / 2);
		return HEIGHT;
	}
	
	protected int drawLineWithIcon(Image image, String line, Graphics gr, GameWindow window, int xTrans) {
		gr.setColor(success ? Color.GREEN : Color.RED);
		Font oldFont = gr.getFont();
		gr.setFont(oldFont.deriveFont(FONT_SIZE));
		Vector pos = getPos(window, xTrans);
		gr.drawImage(image, pos.getX(), pos.getY(), null);
		gr.drawString(line, image.getWidth(null) + pos.getX(), pos.getY() + image.getHeight(null) / 2);
		return image.getHeight(null) + IMAGE_BUFFER;
	}
		
}
