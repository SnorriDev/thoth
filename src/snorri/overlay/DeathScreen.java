package snorri.overlay;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JPanel;

import snorri.main.Main;
import snorri.windows.GamePanel;
import snorri.windows.GameWindow;
import snorri.windows.MenuPanel;

public class DeathScreen extends GamePanel {

	private static final long serialVersionUID = 1L;
	
	public DeathScreen() {
		setLayout(new GridBagLayout());
		setOpaque(false);
		JPanel panel = new MenuPanel();
		panel.setOpaque(false);
		panel.add(createButton("RESPAWN"));
		panel.add(createButton("MAIN MENU"));
		add(panel, new GridBagConstraints());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("MAIN MENU")) {
			Main.launchMenu();
		}
		else if (e.getActionCommand().equals("RESPAWN")) {
			Main.setOverlay(null);
			GameWindow window = (GameWindow) Main.getWindow();
			File directory = window.getWorld().getDirectory();
			Main.launchGame(directory);
		}
	}

}
