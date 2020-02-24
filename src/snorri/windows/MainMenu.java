package snorri.windows;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import snorri.audio.Music;
import snorri.main.Main;
import snorri.main.Util;
import snorri.world.World;

public class MainMenu extends GamePanel {

	private static final long serialVersionUID = 1L;
	
	private static final String PLAY_STORY = "PLAY STORY";
	private static final String LOAD_WORLD = "LOAD WORLD";
	private static final String WORLD_EDITOR = "WORLD EDITOR";
	
	public MainMenu() {
		
		JPanel outerFrame = new JPanel();
		outerFrame.setOpaque(false);
		outerFrame.setLayout(new GridLayout(0, 1));
		add(outerFrame);
		
		outerFrame.add(new JLabel(new ImageIcon(Util.resize(Main.getImage("/textures/titleScreen/title.png"), 0, 310))));
		
		JPanel menu = new MenuPanel();
		menu.add(createButton(PLAY_STORY));
		menu.add(createButton(LOAD_WORLD));
		menu.add(createButton(WORLD_EDITOR));
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
		case PLAY_STORY:
			Main.launchGame("/worlds/tutorial");
			break;
		case LOAD_WORLD:
			File fh = World.wrapLoad();
			if (fh != null) {
				Main.launchGame(fh);
			}
			break;
		case WORLD_EDITOR:
			Main.launchEditor();
		}
				
	}
		
}
