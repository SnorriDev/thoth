package snorri.main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;

public class LoadingScreen extends GamePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoadingScreen() {
		add(new JTextField("Loading..."));
		setVisible(true);
		setFocusable(true);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Main.log("painting");
//		g.fillRect(0, 0, getBounds().width, getBounds().height);
//		g.drawString("Loading...", 50, 50);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	}

}
