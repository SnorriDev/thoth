package snorri.overlay;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.main.MenuPanel;
import snorri.masking.AlphaMask;
import snorri.world.Tile;

public class PauseOverlay extends Overlay {

	private static final long serialVersionUID = 1L;
	
	public PauseOverlay(GameWindow window) {
		super(window);
		setLayout(new GridBagLayout());
		JPanel menu = new MenuPanel();
		menu.setOpaque(false);
		menu.add(createButton("Save"));
		menu.add(createButton("Back"));
		menu.add(createButton("Help"));
		menu.add(createButton("Quit"));
		
		BufferedImage tile = Tile.TileType.GRASS.getTexture(0);
		for (int i = 5; i < 8; i++) {
			BufferedImage image = AlphaMask.getMask(i).getMasked(tile);
			add(new JLabel(new ImageIcon(image)));
		}
		
		add(menu, new GridBagConstraints());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Save")) {
			window.getUniverse().wrapSave();
		}
		if (e.getActionCommand().equals("Back")) {
			window.unpause();
		}
		if (e.getActionCommand().equals("Help")) {
			Main.setOverlay(new HelpOverlay(window));
		}
		if (e.getActionCommand().equals("Quit")) {
			//TODO save
			Main.launchMenu();
		}
	}
	
}
