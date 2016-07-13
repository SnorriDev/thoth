package snorri.main;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import snorri.world.World;

public class MainMenu extends GamePanel {

	private static final long serialVersionUID = 1L;
		
	public MainMenu() {
		JPanel menu = new JPanel(new GridLayout(0, 1));
		menu.add(createButton("New Story"));
		menu.add(createButton("Continue Story"));
		menu.add(createButton("Load World"));
		menu.add(createButton("World Editor"));
		add(menu);
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
