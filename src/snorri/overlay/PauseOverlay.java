package snorri.overlay;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import snorri.keyboard.Key;
import snorri.main.GamePanel;
import snorri.main.GameWindow;
import snorri.main.Main;

public class PauseOverlay extends GamePanel implements KeyListener {

	private static final long serialVersionUID = 1L;
	
	private final GameWindow window;

	public PauseOverlay(GameWindow window) {
		setOpaque(false);
		setLayout(new GridBagLayout());
		JPanel menu = new JPanel(new GridLayout(0, 1));
		menu.setOpaque(false);
		menu.add(createButton("Back"));
		menu.add(createButton("Quit"));
		add(menu, new GridBagConstraints());
		addKeyListener(this);
		this.window = window;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Back")) {
			window.togglePause();
		}
		if (e.getActionCommand().equals("Quit")) {
			//TODO save
			Main.launchMenu();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (Key.ESC.isPressed(e)) {
			window.togglePause();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
	
}
