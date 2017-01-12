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
		outerFrame.setOpaque(false);
		outerFrame.setLayout(new GridLayout(0, 1));
		add(outerFrame);
		
		outerFrame.add(new JLabel(new ImageIcon(Util.resize(Main.getImage("/textures/titleScreen/title.png"), 0, 310))));
		
		JPanel menu = new MenuPanel();
		menu.add(createButton("TUTORIAL"));
		menu.add(createButton("OASIS ADVENTURE"));
		menu.add(createButton("DUNGEON ADVENTURE"));
		menu.add(createButton("LOAD WORLD"));
		menu.add(createButton("WORLD EDITOR"));
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
		case "TUTORIAL":
			Main.launchGame(new WorldSelection("/worlds/tutorial"));
			break;
		case "OASIS ADVENTURE":
			Main.launchGame(new TerrainGen(200, 200));
			break;
		case "DUNGEON ADVENTURE":
			Main.launchGame(new DungeonGen(200, 200));
			break;
		case "LOAD WORLD":
			World w = World.wrapLoad();
			if (w != null) {
				Main.launchGame(w);
			}
			break;
		case "WORLD EDITOR":
			Main.launchEditor();
		}
		
		//TODO: for continue story, look to saved campaign
		
	}
		
}
