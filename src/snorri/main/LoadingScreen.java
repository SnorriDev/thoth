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
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Main.log("painting");
		g.fillRect(0, 0, Main.getBounds().width, Main.getBounds().height);
		g.drawString("Loading...", 50, 50);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
