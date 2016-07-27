package snorri.overlay;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import snorri.keyboard.Key;
import snorri.main.GamePanel;
import snorri.main.GameWindow;

public abstract class Overlay extends GamePanel implements KeyListener {

	protected static final Color NORMAL_BG = new Color(255, 179, 71);
	protected static final Color SELECTED_BG = new Color(255, 150, 71);
	protected static final Color BORDER = new Color(255, 130, 71);
	
	protected final GameWindow window;
	
	protected Overlay(GameWindow window) {
		this.window = window;
		setOpaque(false);
		addKeyListener(this);
	}
	
	private static final long serialVersionUID = 1L;

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (Key.ESC.isPressed(e)) {
			window.unpause();
		}
	}
	
}
