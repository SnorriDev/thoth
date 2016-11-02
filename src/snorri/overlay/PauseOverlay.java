package snorri.overlay;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.main.MenuPanel;

public class PauseOverlay extends Overlay {

	private static final long serialVersionUID = 1L;
	
	public PauseOverlay(FocusedWindow focusedWindow) {
		super(focusedWindow);
		setLayout(new GridBagLayout());
		JPanel menu = new MenuPanel();
		menu.setOpaque(false);
		menu.add(createButton("Save"));
		menu.add(createButton("Back"));
		menu.add(createButton("Objective"));
		menu.add(createButton("Help"));
		menu.add(createButton("Quit"));
		
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
		if (e.getActionCommand().equals("Objective")) {
			Main.setOverlay(new ObjectiveOverlay(window));
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
