package snorri.dialog;

import java.awt.Graphics;

import snorri.inventory.Timer;
import snorri.main.GameWindow;

public abstract class Message {

	protected static final double LENGTH = 2.5;
	protected static final int TRANS_DOWN = 46;
	protected static final int HEIGHT = 23;
	protected static final int IMAGE_BUFFER = 5;
	protected static final float FONT_SIZE = 23;
	
	protected final Timer timer;

	protected Message() {
		timer = new Timer(LENGTH);
		timer.hardReset();
	}
	
	public boolean update(double deltaTime) {
		timer.update(deltaTime);
		return timer.isOffCooldown();
	}
	
	public abstract int render(GameWindow window, Graphics gr, int xTrans);
	
}
