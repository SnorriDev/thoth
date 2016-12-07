package snorri.main;

import java.awt.GridLayout;

import javax.swing.JPanel;

public class MenuPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public MenuPanel() {
		GridLayout layout = new GridLayout(0, 1);
		layout.setVgap(10);
		setLayout(layout);
		setOpaque(false);
	}
	
}
