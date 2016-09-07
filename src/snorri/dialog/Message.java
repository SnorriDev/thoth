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
	protected static final int TRANS_DOWN = 65;
	protected static final int HEIGHT = 28;
	protected static final int IMAGE_BUFFER = 5;
	protected static final int ICON_SIZE = 20;
	protected static final int BOX_BUFFER = 3;
	
	protected static final Color BACKGROUND = new Color(222, 196, 169, 230);
	
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
			icon = Util.resize(image, -1, ICON_SIZE);
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
		return drawLine(gr, window, getPos(window, xTrans), success ? Color.GREEN : Color.RED);
	}
	
	protected int drawLine(Graphics gr, GameWindow window, Vector pos, Color color) {
		int width = getLineWidth(gr, toString()) + 2 * BOX_BUFFER;
		gr.setColor(BACKGROUND);
		gr.fillRect(pos.getX(), pos.getY(), width + 2 * BOX_BUFFER, ICON_SIZE + 2 * BOX_BUFFER);
		gr.setColor(color);
		gr.drawRect(pos.getX(), pos.getY(), width + 2 * BOX_BUFFER, ICON_SIZE + 2 * BOX_BUFFER);
		gr.drawString(toString(), pos.getX() + BOX_BUFFER, pos.getY() + HEIGHT / 2 + BOX_BUFFER);
		return HEIGHT;
	}
	
	protected int drawLineWithIcon(String line, Graphics gr, GameWindow window, int xTrans) {
		Vector pos = getPos(window, xTrans);
		int width = icon.getWidth(null) + getLineWidth(gr, line) + IMAGE_BUFFER + 2 * BOX_BUFFER;
		gr.setColor(BACKGROUND);
		gr.fillRect(pos.getX(), pos.getY(), width + 2 * BOX_BUFFER, ICON_SIZE + 2 * BOX_BUFFER);
		gr.setColor(success ? Color.GREEN : Color.RED);
		gr.drawRect(pos.getX(), pos.getY(), width + 2 * BOX_BUFFER, ICON_SIZE + 2 * BOX_BUFFER);
		gr.drawImage(icon, pos.getX() + BOX_BUFFER, pos.getY() + BOX_BUFFER, null);
		gr.drawString(line, icon.getWidth(null) + BOX_BUFFER + IMAGE_BUFFER + pos.getX(), pos.getY() + BOX_BUFFER + icon.getHeight(null) / 2);
		return icon.getHeight(null) + IMAGE_BUFFER + 2 * BOX_BUFFER;
	}
	
	protected static final int getLineWidth(Graphics gr, String line) {
		return gr.getFontMetrics().stringWidth(line);
	}
		
}
