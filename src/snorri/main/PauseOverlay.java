package snorri.main;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import snorri.world.Vector;

public class PauseOverlay extends GamePanel {

	private static final long serialVersionUID = 1L;
	
	private final GameWindow window;

	public PauseOverlay(GameWindow window) {
		setLayout(new GridLayout(0, 1));
		setOpaque(false);
		add(createButton("Back", new Vector(100, 20)));
		this.window = window;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Back")) {
			window.togglePause();
		}
	}
	
}
