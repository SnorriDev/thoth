package snorri.main;

import java.awt.event.ActionEvent;

import snorri.world.World;

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
		case "Load World":
			World w2 = World.wrapLoad();
			if (w2 != null) {
				Main.launchGame(w2);
			}
			break;
		case "World Editor":
			Main.launchEditor();
		}
		
		//TODO: for continue story, look to saved campaign
		
	}
		
}
