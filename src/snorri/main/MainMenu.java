package snorri.main;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import snorri.audio.Music;
import snorri.terrain.DungeonGen;
import snorri.terrain.TerrainGen;
import snorri.world.World;

public class MainMenu extends GamePanel {

	private static final long serialVersionUID = 1L;
		
	public MainMenu() {
		
		JPanel outerFrame = new JPanel();
		outerFrame.setLayout(new GridLayout(0, 1));
		add(outerFrame);
		
		outerFrame.add(new JLabel(new ImageIcon(Util.resize(Main.getImage("/textures/hud/title.png"), 0, 310))));
		
		JPanel menu = new MenuPanel();
		menu.add(createButton("Tutorial"));
		menu.add(createButton("Oasis Adventure"));
		menu.add(createButton("Dungeon Adventure"));
		menu.add(createButton("Load World"));
		menu.add(createButton("World Editor"));
		outerFrame.add(menu);
		
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
			World w = World.wrapLoad();
			if (w != null) {
				Main.launchGame(w);
			}
			break;
		case "World Editor":
			Main.launchEditor();
		}
		
		//TODO: for continue story, look to saved campaign
		
	}
		
}
