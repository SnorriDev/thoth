package snorri.overlay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;

import snorri.keyboard.Key;
import snorri.main.FocusedWindow;
import snorri.main.GamePanel;
import snorri.main.Main;

public abstract class Overlay extends GamePanel implements KeyListener {
	
	//private final Color GRAYED_OUT = new Color(50, 50, 50, 50);
	protected static final Color NORMAL_BG = new Color(255, 179, 71);
	protected static final Color SELECTED_BG = new Color(255, 150, 71);
	protected static final Color BORDER = new Color(255, 130, 71);
	private static final Image BACKGROUND = Main.getImage("/textures/hud/textBox.png");
	
	protected final FocusedWindow window;
	
	protected class TextPane extends JTextPane {

		private static final long serialVersionUID = 1L;
		
		public TextPane() {
			setContentType("text/html");
			setEditable(false);
			if (Overlay.this instanceof KeyListener) {
				addKeyListener((KeyListener) Overlay.this);
			}
			setBorder(BorderFactory.createLineBorder(BORDER));
			setBackground(NORMAL_BG);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			g.drawImage(BACKGROUND, 0, 0, null);
			super.paintComponent(g);
		}
		
	}
	
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
