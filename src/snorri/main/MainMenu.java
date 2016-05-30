package snorri.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class MainMenu extends GamePanel implements ActionListener {

	private static final long serialVersionUID = 1L;
		
	public MainMenu() {
		createButton("Play");
		createButton("Map Editor");
	}
	
	private JButton createButton(String text) {
		JButton button = new JButton(text);
		add(button);
		button.setSelected(false);
		button.addActionListener(this);
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
		case "Play":
			Main.launchGame();
			break;
		case "Map Editor":
			Main.launchEditor();
		}
		
	}
		
}
