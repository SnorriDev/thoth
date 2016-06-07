package snorri.main;

import java.awt.event.ActionEvent;

public class MainMenu extends GamePanel {

	private static final long serialVersionUID = 1L;
		
	public MainMenu() {
		//setLayout(new BorderLayout());
		createButton("New Story");
		createButton("Continue Story");
		createButton("Load World");
		createButton("World Editor");
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
