package snorri.overlay;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import snorri.keyboard.Key;
import snorri.main.FocusedWindow;
import snorri.main.GamePanel;
import snorri.main.Main;

public abstract class Overlay extends GamePanel implements KeyListener {
	
	
	private static final long serialVersionUID = 1L;
	
	private static final Image BACKGROUND = Main.getImage("/textures/hud/textBox.png");
	
	protected final FocusedWindow window;
	
	protected class TextPane extends JPanel {

		private static final long serialVersionUID = 1L;
		private final JTextPane pane;
				
		public TextPane() {
			
			setOpaque(false);
			setPreferredSize(new Dimension(BACKGROUND.getWidth(null), BACKGROUND.getHeight(null)));
			setBorder(emptyBorder());
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			pane = new JTextPane();
			pane.setEditorKit(getHTMLEditorKit());
			pane.setOpaque(false);
			pane.setEditable(false);
			pane.setMaximumSize(new Dimension(350, 10000));
			
			JScrollPane scroll = new JScrollPane(pane);
			scroll.setPreferredSize(new Dimension(350, 175));
			scroll.setOpaque(false);
			scroll.getViewport().setOpaque(false);
			scroll.setBorder(null);
			scroll.setViewportBorder(null);
			c.gridx = 0;
			c.gridy = 0;
			add(scroll, c);
			
			JButton b = createButton("Okay");
			c.gridx = 0;
			c.gridy = 1;
			add(b, c);
			
			if (Overlay.this instanceof KeyListener) {
				addKeyListener((KeyListener) Overlay.this);
				pane.addKeyListener((KeyListener) Overlay.this);
				b.addKeyListener((KeyListener) Overlay.this);
			}
			
		}
		
		@Override
		public void paintComponent(Graphics g) {
			g.drawImage(BACKGROUND, 0, 0, null);
			super.paintComponent(g);
		}
		
		public void setPage(URL url) throws IOException {
			pane.setPage(url);
		}
		
		public void setText(String text) {
			pane.setText(text);
		}
		
	}
	
	protected Overlay(FocusedWindow focusedWindow) {
		this.window = focusedWindow;
		setOpaque(false);
		addKeyListener(this);
	}
	
	public static Border emptyBorder(int xPad, int yPad) {
		return BorderFactory.createEmptyBorder(16 + xPad, 16 + yPad, 16, 16);
	}
	
	public static Border emptyBorder() {
		return emptyBorder(0, 0);
	}
	
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
	
	public HTMLEditorKit getHTMLEditorKit() {
		HTMLEditorKit kit = new HTMLEditorKit();
		try {
			StyleSheet s = new StyleSheet();
			s.importStyleSheet(Main.getFile("/info/style.css").toURI().toURL());
			kit.setStyleSheet(s);
		} catch (MalformedURLException e) {
			Main.log("could not load stylesheet");
		}
		return kit;
	}
	
}
