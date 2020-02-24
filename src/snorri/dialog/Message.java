package snorri.dialog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import snorri.inventory.Timer;
import snorri.main.Util;
import snorri.windows.GamePanel;
import snorri.windows.GameWindow;
import snorri.world.Vector;

public abstract class Message {

	protected static final double LENGTH = 2.5;
	protected static final int HEIGHT = 32;
	protected static final int IMAGE_BUFFER = 5;
	protected static final int ICON_SIZE = 20;
	protected static final int BOX_BUFFER = 3;
	
	protected static final Color BACKGROUND = new Color(222, 196, 169, 230);
	
	protected final Timer timer;
	protected Runnable onClear = null;
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
	
	protected static Vector getBasePos(GameWindow window, int width) {
		return new Vector(window.getCenter().getX() - width / 2, GamePanel.MARGIN + HEIGHT);
	}
	
	protected static Vector getPos(GameWindow window, int width, int xTrans) {
		return getBasePos(window, width).add_(0, xTrans);
	}
	
	public abstract String toString();
	
	public int render(GameWindow window, Graphics gr, int xTrans) {
		return drawLine(gr, window, xTrans);
	}
	
	protected int drawLine(Graphics gr, GameWindow window, int xTrans) {
		return drawLine(gr, window, xTrans, getSuccessColor());
	}
	
	protected int drawLine(Graphics gr, GameWindow window, int xTrans, Color color) {
		int width = getLineWidth(gr, toString()) + 2 * BOX_BUFFER;
		Vector pos = getPos(window, width, xTrans);
		gr.setColor(BACKGROUND);
		gr.fillRect(pos.getX(), pos.getY(), width + 2 * BOX_BUFFER, ICON_SIZE + 2 * BOX_BUFFER);
		gr.setColor(color);
		gr.drawRect(pos.getX(), pos.getY(), width + 2 * BOX_BUFFER, ICON_SIZE + 2 * BOX_BUFFER);
		gr.drawString(toString(), pos.getX() + BOX_BUFFER, pos.getY() + HEIGHT / 2 + BOX_BUFFER);
		return HEIGHT;
	}
	
	protected int drawLineWithIcon(String line, Graphics gr, GameWindow window, int xTrans) {
		return this.drawLineWithIcon(line, gr, window, xTrans, getSuccessColor());
	}
	
	protected int drawLineWithIcon(String line, Graphics gr, GameWindow window, int xTrans, Color color) {
		int width = getLineWidth(gr, toString()) + 2 * BOX_BUFFER + icon.getWidth(null) + IMAGE_BUFFER;
		Vector pos = getPos(window, width, xTrans);
		gr.setColor(BACKGROUND);
		gr.fillRect(pos.getX(), pos.getY(), width, ICON_SIZE + 2 * BOX_BUFFER);
		gr.setColor(color);
		gr.drawRect(pos.getX(), pos.getY(), width, ICON_SIZE + 2 * BOX_BUFFER);
		gr.drawImage(icon, pos.getX() + BOX_BUFFER, pos.getY() + BOX_BUFFER, null);
		gr.drawString(line, icon.getWidth(null) + BOX_BUFFER + IMAGE_BUFFER + pos.getX(), pos.getY() + HEIGHT / 2 + BOX_BUFFER);
		return HEIGHT;
	}
	
	protected static final int getLineWidth(Graphics gr, String line) {
		if (line == null) {
			return 4;
		}
		return gr.getFontMetrics().stringWidth(line);
	}
	
	protected Color getSuccessColor() {
		return success ? Color.GREEN : Color.RED;
	}
	
	public boolean hasIcon() {
		return icon != null;
	}
	
	public void onClear() {
		if (onClear != null) {
			onClear.run();
		}
	}
		
}
