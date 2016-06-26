package snorri.main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class LoadingScreen extends GamePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel label;

	public LoadingScreen() {
		label = new JLabel("Loading...");
		add(new JLabel(new ImageIcon(Main.getImageResource("/textures/conceptArt/figure.png"))));
		add(label);
		setVisible(true);
		setFocusable(true);
	}
	
	public void setText(String text) {
		label.setText(text);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
//		g.fillRect(0, 0, getBounds().width, getBounds().height);
//		g.drawString("Loading...", 50, 50);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	}

}
