package snorri.overlay;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import snorri.main.Main;
import snorri.windows.GamePanel;

public class DeathScreen extends GamePanel {

	private static final long serialVersionUID = 1L;
	
	public DeathScreen() {
		setLayout(new GridBagLayout());
		setOpaque(false);
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.setOpaque(false);
		panel.add(createButton("MAIN MENU"));
		add(panel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("MAIN MENU")) {
			Main.launchMenu();
		}
	}

}
