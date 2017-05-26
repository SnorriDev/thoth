package snorri.overlay;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import snorri.dialog.Dialog;
import snorri.keyboard.Key;
import snorri.main.FocusedWindow;
import snorri.main.GamePanel;
import snorri.main.GameWindow;
import snorri.main.Main;

public abstract class Overlay extends GamePanel implements KeyListener {
	
	private static final Image TEXT_BOX = Main.getImage("/textures/hud/textBox.png");
	private static final Image DIALOG_BOX = Main.getImage("/textures/hud/dialogBox.png");
	
	private static final long serialVersionUID = 1L;
		
	protected final FocusedWindow window;
	
	protected abstract class BoxPane extends JPanel {
		
		private static final long serialVersionUID = 1L;
		private Image box;
		
		protected BoxPane(Image box) {
			
			this.box = box;
			setOpaque(false);
			setPreferredSize(new Dimension(box.getWidth(null), box.getHeight(null)));
			setBorder(emptyBorder());
			setLayout(new GridBagLayout());
			
			if (Overlay.this instanceof KeyListener) {
				addKeyListener((KeyListener) Overlay.this);
			}
			
		}
		
		@Override
		public void paintComponent(Graphics g) {
			g.drawImage(box, 0, 0, null);
			super.paintComponent(g);
		}
		
	}
	
	protected class TextPane extends BoxPane {

		private static final long serialVersionUID = 1L;
		
		protected final JTextPane pane;
		
		public TextPane() {
			
			super(TEXT_BOX);
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
			
			JButton b = createButton("OKAY");
			c.gridx = 0;
			c.gridy = 1;
			add(b, c);
			
			if (Overlay.this instanceof KeyListener) {
				pane.addKeyListener((KeyListener) Overlay.this);
				b.addKeyListener((KeyListener) Overlay.this);
			}
			
		}
		
		public void setPage(URL url) throws IOException {
			pane.setPage(url);
		}
		
		public void setText(String text) {
			pane.setText(text);
		}
		
	}
	
	protected class DialogPane extends BoxPane {
		
		private static final long serialVersionUID = 1L;
		private static final int MARGIN = 10;
		private static final int WIDTH = 650;
		private static final int HEIGHT = 160;
		
		protected final JTextPane pane;

		public DialogPane(Dialog dialog) {
			
			super(DIALOG_BOX);
			GridBagConstraints c = new GridBagConstraints();
			
			//FIXME adjust the y-axis padding and margins
			
			JLabel portrait = new JLabel(dialog.getIcon());
			c.gridx = 0;
			c.gridy = 0;
			c.weighty = 1;
			add(portrait, c);
			
			JLabel name = new JLabel(dialog.getName());
			name.setFont(name.getFont().deriveFont(Font.BOLD, 16));
			c.gridx = 0;
			c.gridy = 1;
			c.weighty = 2;
			add(name, c);
			
			pane = new JTextPane();
			pane.setEditorKit(getHTMLEditorKit());
			pane.setOpaque(false);
			pane.setEditable(false);
			if (dialog.showObjective && Main.getWindow() instanceof GameWindow) {
				pane.setText(dialog.text + "\n\n" + ((GameWindow) Main.getWindow()).getObjectiveInfo());
			} else {
				pane.setText(dialog.text);
			}
			pane.setMargin(new Insets(0, MARGIN, 0, 0));
			pane.setMaximumSize(new Dimension(WIDTH, 1000000));
			
			JScrollPane scroll = new JScrollPane(pane);
			scroll.setPreferredSize(new Dimension(WIDTH, HEIGHT));
			scroll.setOpaque(false);
			scroll.getViewport().setOpaque(false);
			scroll.setBorder(null);
			scroll.setViewportBorder(null);
			c.gridx = 1;
			c.gridy = 0;
			c.weighty = 0;
			c.gridheight = 2;
			add(scroll, c);
			
			if (Overlay.this instanceof KeyListener) {
				pane.addKeyListener((KeyListener) Overlay.this);
			}
			
		}
		
	}
	
	protected Overlay(FocusedWindow focusedWindow) {
		super();
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
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (Key.ESC.isPressed(e)) {
			window.unpause();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OKAY")) {
			window.unpause();
		}
	}
		
	public HTMLEditorKit getHTMLEditorKit() {
		HTMLEditorKit kit = new HTMLEditorKit();
		try {
			StyleSheet s = new StyleSheet();
			s.setBase(Main.getDir().toURI().toURL());
			s.importStyleSheet(Main.getFile("/info/style.css").toURI().toURL());
			kit.setStyleSheet(s);
		} catch (MalformedURLException e) {
			Main.log("could not load stylesheet");
		}
		return kit;
	}
	
}
