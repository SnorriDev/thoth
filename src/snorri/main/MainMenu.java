package snorri.main;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import snorri.audio.Music;
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
		menu.add(createButton("PLAY STORY"));
//		menu.add(createButton("OASIS ADVENTURE"));
//		menu.add(createButton("DUNGEON ADVENTURE"));
		menu.add(createButton("LOAD WORLD"));
		menu.add(createButton("WORLD EDITOR"));
		outerFrame.add(menu);
		
		Music.MAIN_THEME.play();
		
	}

	@Override
	public void stopBackgroundThread() {
		Music.MAIN_THEME.stop();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
		case "TUTORIAL":
			Main.launchGame("/worlds/tutorial");
			break;
		case "LOAD WORLD":
			File fh = World.wrapLoad();
			if (fh != null) {
				Main.launchGame(fh);
			}
			break;
		case "WORLD EDITOR":
			Main.launchEditor();
		}
				
	}
		
}
