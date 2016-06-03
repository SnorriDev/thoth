package snorri.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class MainMenu extends GamePanel implements ActionListener {

	private static final long serialVersionUID = 1L;
		
	public MainMenu() {
		//setLayout(new BorderLayout());
		createButton("New Story");
		createButton("Continue Story");
		createButton("Load World");
		createButton("World Editor");
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
		case "New Story":
			Main.launchGame();
			break;
		case "World Editor":
			Main.launchEditor();
		}
		
	}
		
}
