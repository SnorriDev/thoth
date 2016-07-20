package snorri.main;

import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import snorri.world.World;

public class MainMenu extends GamePanel {

	private static final long serialVersionUID = 1L;
		
	public MainMenu() {
		
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel menu = new MenuPanel();
		
		JLabel title = new JLabel("The Book of Thoth");
		title.setFont(Main.getCustomFont(80));
		menu.add(title);
		
		menu.add(new JLabel(""));
		
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
