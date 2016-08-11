package snorri.dialog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import snorri.inventory.Timer;
import snorri.main.GameWindow;
import snorri.main.Util;
import snorri.world.Vector;

public abstract class Message {

	protected static final double LENGTH = 2.5;
	protected static final int TRANS_DOWN = 56;
	protected static final int HEIGHT = 28;
	protected static final int IMAGE_BUFFER = 5;
	protected static final int ICON_SIZE = 20;
	
	protected final Timer timer;
	protected boolean success = true;
	
	private Image icon;

	protected Message() {
		timer = new Timer(LENGTH);
		timer.hardReset();
	}
	
	protected Message(Image image) {
		this();
		if (image != null) {
			icon = Util.resize(image, -1, 20);
		}
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
		Vector pos = getPos(window, xTrans);
		gr.drawString(this.toString(), pos.getX() + HEIGHT / 2, pos.getY() + HEIGHT / 2);
		return HEIGHT;
	}
	
	protected int drawLineWithIcon(String line, Graphics gr, GameWindow window, int xTrans) {
		gr.setColor(success ? Color.GREEN : Color.RED);
		Vector pos = getPos(window, xTrans);
		gr.drawImage(icon, pos.getX(), pos.getY(), null);
		gr.drawString(line, icon.getWidth(null) + IMAGE_BUFFER + pos.getX(), pos.getY() + icon.getHeight(null) / 2);
		return icon.getHeight(null) + IMAGE_BUFFER;
	}
		
}
