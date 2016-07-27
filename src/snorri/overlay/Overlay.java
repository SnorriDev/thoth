package snorri.overlay;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import snorri.keyboard.Key;
import snorri.main.GamePanel;
import snorri.main.GameWindow;

public abstract class Overlay extends GamePanel implements KeyListener {

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
