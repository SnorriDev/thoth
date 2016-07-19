package snorri.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import snorri.inventory.Timer;
import snorri.world.Vector;

public class DialogMessage {

	private static final double LENGTH = 2.5;
	private static final int TRANS_DOWN = 46;
	private static final float FONT_SIZE = 23;
	private static final int HEIGHT = 23;
	
	private String msg;
	private Timer timer;
	
	public DialogMessage(String msg) {
		this.msg = msg;
		timer = new Timer(LENGTH);
		timer.hardReset();
	}
	
	public boolean update(double deltaTime) {
		timer.update(deltaTime);
		return timer.isOffCooldown();
	}
	
	public void render(GameWindow window, Graphics gr, int i) {
		Vector pos = window.getFocus().getHealthBarPos().add(0, TRANS_DOWN + i * HEIGHT);
		gr.setColor(Color.GREEN);
		Font oldFont = gr.getFont();
		gr.setFont(oldFont.deriveFont(FONT_SIZE));
		gr.drawString(msg, pos.getX(), pos.getY());
		gr.setFont(oldFont);
		gr.setColor(Color.BLACK);
	}
	
}
