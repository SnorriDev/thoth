package snorri.main;

import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import snorri.audio.Music;
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
		
		menu.add(createButton("Tutorial"));
		menu.add(createButton("Oasis Adventure"));
		menu.add(createButton("Dungeon Adventure"));
		menu.add(createButton("Load World"));
		menu.add(createButton("World Editor"));
		add(menu);
		
		Music.MAIN_THEME.play();
		
	}

	@Override
	public void onClose() {
		Music.MAIN_THEME.stop();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
		case "Tutorial":
			Main.launchGame(new WorldSelection("/worlds/tutorial"));
			break;
		case "Oasis Adventure":
			Main.launchGame(new TerrainGen(200, 200));
			break;
		case "Dungeon Adventure":
			Main.launchGame(new DungeonGen(200, 200));
			break;
		case "Load World":
			Main.launchGame(World.wrapLoad());
			break;
		case "World Editor":
			Main.launchEditor();
		}
		
		//TODO: for continue story, look to saved campaign
		
	}
		
}
