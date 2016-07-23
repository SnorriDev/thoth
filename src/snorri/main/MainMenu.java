package snorri.main;

import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import snorri.terrain.DungeonGen;
import snorri.terrain.TerrainGen;
import snorri.world.World;

public class MainMenu extends GamePanel {

	private static final long serialVersionUID = 1L;
		
	public MainMenu() {
				
		JPanel menu = new MenuPanel();
		
		JLabel title = new JLabel("The Book of Thoth");
		title.setFont(Main.getCustomFont(80));
		menu.add(title);
		
		menu.add(new JLabel("")); //for spacing
		
		menu.add(createButton("Random Delta"));
		menu.add(createButton("Random Dungeon"));
		menu.add(createButton("Load World"));
		menu.add(createButton("World Editor"));
		add(menu);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
		case "Random Delta":
			Main.launchGame(new TerrainGen(200, 200));
			break;
		case "Random Dungeon":
			Main.launchGame(new DungeonGen(200, 200));
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
