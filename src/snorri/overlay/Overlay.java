package snorri.overlay;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import snorri.keyboard.Key;
import snorri.main.FocusedWindow;
import snorri.main.GamePanel;

public abstract class Overlay extends GamePanel implements KeyListener {
	
	//private final Color GRAYED_OUT = new Color(50, 50, 50, 50);
	
	protected final FocusedWindow window;
	
	protected Overlay(FocusedWindow focusedWindow) {
		this.window = focusedWindow;
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
	
//	@Override
//	public void paintComponent(Graphics g) {
//		Dimension size = getSize();
//		Main.log("drawing shit");
//		g.setColor(Color.BLACK);
//		g.drawRect(0, 0, size.width, size.height);
//		super.paintComponent(g);
//	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (Key.ESC.isPressed(e)) {
			window.unpause();
		}
	}
	
}
